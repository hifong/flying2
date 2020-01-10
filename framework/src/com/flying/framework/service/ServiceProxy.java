package com.flying.framework.service;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import net.sf.cglib.core.ClassGenerator;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.transform.TransformingClassGenerator;
import net.sf.cglib.transform.impl.AddPropertyTransformer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.objectweb.asm.Type;

import com.flying.common.log.Logger;
import com.flying.common.util.Codes;
import com.flying.common.util.Pair;
import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.service.Autowired;
import com.flying.framework.async.Publisher;
import com.flying.framework.config.EventConfig;
import com.flying.framework.config.EventHandler;
import com.flying.framework.config.ServiceConfig;
import com.flying.framework.data.Data;
import com.flying.framework.exception.ValidationError;
import com.flying.framework.exception.ValidationException;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceDescriptor.ServiceMethodParameter;
import com.flying.framework.util.ValidationUtils;

@SuppressWarnings("rawtypes")
public class ServiceProxy implements MethodInterceptor {
	private final static Logger logger = Logger.getLogger(ServiceProxy.class);

	protected final LocalModule module;
	protected final ServiceConfig serviceConfig;

	private final Enhancer enhancer = new Enhancer();
	protected final Object serviceObject;
	protected final Class<?> serviceClass;
	protected ServiceDescriptor serviceDescriptor;
	
	private boolean IOCInitialized = false;

	public ServiceProxy(LocalModule module, ServiceConfig serviceConfig, Class<?> cls) {
		this.module = module;
		this.serviceConfig = serviceConfig;
		this.serviceClass = cls;

		enhancer.setClassLoader(module.getClassLoader());
		enhancer.setSuperclass(cls);
		enhancer.setCallback(this);
		enhancer.setStrategy(new DefaultGeneratorStrategy() {
		    protected ClassGenerator transform(ClassGenerator cg) {
				List<Pair<String, Class>> props = Utils.newArrayList();
				//
				Field[] fields = serviceClass.getDeclaredFields();
				for(int i=0; i< fields.length; i++) {
					final Field f = fields[i];
					Autowired autowired = f.getAnnotation(Autowired.class);
					if(autowired != null && autowired.required()) {
						props.add(new Pair<String, Class>(f.getName(), f.getType()));
					}
				}
				//
				final String[] names = new String[props.size()];
				final Type[] types = new Type[props.size()];
				for(int i=0; i< names.length; i++) {
					names[i] = props.get(i).getKey();
					types[i] = Type.getType(props.get(i).getValue());
				}
				//
		    	return new TransformingClassGenerator(cg, new AddPropertyTransformer(names, types));
		    }});

		serviceObject = enhancer.create();
		//
		if (PropertyUtils.isWriteable(serviceObject, "module"))
			try {
				BeanUtils.setProperty(serviceObject, "module", module);
			} catch (Exception e) {
				logger.warn("ServiceProxy.createInstance setModule fail!", e);
			}
		if (PropertyUtils.isWriteable(serviceObject, "serviceConfig"))
			try {
				BeanUtils.setProperty(serviceObject, "serviceConfig", serviceConfig);
			} catch (Exception e) {
				logger.warn("ServiceProxy.createInstance setServiceConfig fail!", e);
			}
	}

	public Object getInstance() {
		return serviceObject;
	}
	
	public ServiceDescriptor getServiceDescriptor() {
		if(this.serviceDescriptor == null) 
			this.serviceDescriptor = new ServiceDescriptor(module, serviceClass);
		return this.serviceDescriptor;
	}

	@Override
	public Object intercept(Object service, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		if(!this.module.isWorking()) return new Data(Codes.CODE, Codes.WORKING_NONE);
		//
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(module.getClassLoader());
		try {
			if (method.getReturnType() != null && (Data.class == method.getReturnType() || Data.class.isAssignableFrom(method.getReturnType()) )) {
				//
				if(!IOCInitialized) {
					this.iocInitialize();
				}
				//
				List<ServiceMethodParameter> params = this.getServiceDescriptor().getMethodParameters(method);
				// Handle InteceptorChain,orginze input
				Data request = new Data();
				for(int i=0; i < params.size(); i ++) {
					ServiceMethodParameter mp = params.get(i);
					Parameter p = mp.getParameter();
					Param pa = mp.getParam();
					if(pa == null && (p.getType() == Data.class || p.getType().isAssignableFrom(Data.class))) {
						if(args[i] != null)
							request = ((Data)args[i]).merge(request, true);
					} else {
						String key = pa == null?p.getName():pa.value();
						request.put(key, args[i]);
					}
				}
				
				// validation check
				Map<String, List<ValidationError>> errors = Utils.newHashMap();
				for(ServiceMethodParameter mp: params) {
					if(mp.getParam() == null) continue;
					final String field = mp.getParam().value();
					Class type = mp.getParameter().getType();
					List<ValidationError> ers = ValidationUtils.checkValidation(field, type, mp.getParam(), request);
					if(!ers.isEmpty()) errors.put(field, ers);
				}
				if(!errors.isEmpty())
					throw new ValidationException(this.module.getId(), this.serviceConfig.getId(), method.getName(), errors);
				//
				ServiceHandlerContext context = this.createServiceContext(method, service, methodProxy, args);
				Data data = context.doChain(request);
				this.triggerEvent(context, data);
				return data;
			} else {
				return methodProxy.invokeSuper(service, args);
			}
		} finally {
			Thread.currentThread().setContextClassLoader(threadClassLoader);
		}
	}

	private void triggerEvent(ServiceHandlerContext context, Data data) {		// 驱动定义的事件
		List<EventConfig> eventConfigs = serviceConfig.getEventConfigsBySender(context.getMethodName());
		for (EventConfig eventConfig : eventConfigs)
			for (EventHandler eventHandler : eventConfig.getHandlers()) {
				if (eventHandler.canHandle(data)) {
					try {
						Publisher.publish(
								StringUtils.isEmpty(eventHandler.getModuleId())?this.module.getId(): eventHandler.getModuleId(), 
										eventHandler.getServiceId(), data);
					} catch (Exception e) {
						logger.error("ServiceProxy.triggerEvent error for " + e, e);
					}
				}
			}
	}
	
	private ServiceHandlerContext createServiceContext(final Method method, final Object object, final MethodProxy mp, 
			final Object[] args) {
		final ServiceHandlerContext chain = new ServiceHandlerContext(this, method, new MethodInvokerHandler(object, mp, args));
		return chain;
	}

	class MethodInvokerHandler implements ServiceHandler {
		private final MethodProxy methodProxy;
		private final Object[] args;
		private final Object object;

		public MethodInvokerHandler(Object object, MethodProxy mp, Object[] args) {
			this.object = object;
			this.methodProxy = mp;
			this.args = args;
		}

		@Override
		public Data handle(Annotation annotation, Data request, ServiceHandlerContext context) throws Exception {
			try {
				// 执行方法体
				Object result = methodProxy.invokeSuper(object, args);
				if (result instanceof Data) {
					return (Data) result;
				}
				return null;
			} catch (Throwable t) {
				if (t instanceof Exception)
					throw (Exception) t;
				else
					throw new RuntimeException(t);
			}
		}

	}
	
	private synchronized void iocInitialize() {
		if(this.IOCInitialized) return;
		PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(this.serviceClass);
		if(pds == null) return;
		for(PropertyDescriptor pd: pds) {
			Class<?> type = pd.getPropertyType();
			if(type == null) continue;
			if(type.getName().startsWith("java") || type.getName().startsWith("org"))  continue;
			if(module.getModuleConfig().existsServiceConfig(type.getName())) {
				try {
					pd.getWriteMethod().invoke(this.serviceObject, new Object[]{module.getService(type)});
					logger.debug(this.serviceClass.getName()+" injectReferenceService ‘"+pd.getName()+"’success！");
				} catch (Exception e) {
					logger.error(this.serviceClass.getName()+" injectReferenceService ‘"+pd.getName()+"’失败，可能导致部分功能无法正常使用！", e);
				}
			}
		}
		this.IOCInitialized = true;
	}
}
