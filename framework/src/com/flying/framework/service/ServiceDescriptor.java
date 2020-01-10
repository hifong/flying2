package com.flying.framework.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.ConstantEnum;
import com.flying.framework.annotation.MethodInfo;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.Service;
import com.flying.framework.annotation.service.Handler;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

@SuppressWarnings("rawtypes")
public class ServiceDescriptor {
	private final LocalModule module;
	private final Class<?> serviceClass;
	private Service serviceAnnotation;
	private Map<Method, ServiceMethod> serviceMethods = Utils.newHashMap();
	
	public ServiceDescriptor(LocalModule module, Class<?> serviceClass) {
		this.module = module;
		if(serviceClass.getName().indexOf("$$EnhancerByCGLIB$$") >= 0)
			this.serviceClass = serviceClass.getSuperclass();
		else
			this.serviceClass = serviceClass;
		//
		this.init();
	}
	
	public Class<?> getServiceClass() {
		return this.serviceClass;
	}
	
	public Service getServiceAnnotation() {
		return this.serviceAnnotation;
	}
	
	private void init() {
		this.serviceAnnotation = this.serviceClass.getAnnotation(Service.class);
		//
		Method[] methods = this.serviceClass.getMethods();
		for(Method m: methods) {
			if(Data.class.isAssignableFrom(m.getReturnType())) {
				this.serviceMethods.put(m, new ServiceMethod(m));
			}
		}
		//
		methods = this.serviceClass.getDeclaredMethods();
		for(Method m: methods) {
			if(this.serviceMethods.containsKey(m)) {
				continue;
			}
			if(Data.class.isAssignableFrom(m.getReturnType())) {
				this.serviceMethods.put(m, new ServiceMethod(m));
			}
		}
	}
	
	public List<AnnotationHandler> getMethodAnnotationHandlers(Method method) {
		ServiceMethod sm = this.serviceMethods.get(method);
		return sm.handlers;
	}
	
	public List<ServiceMethodParameter> getMethodParameters(Method method) {
		return this.serviceMethods.get(method).getParameters();
	}
	
	public class ServiceMethod {
		private final Method method;
		private final MethodInfo methodInfo;
		private final List<AnnotationHandler> handlers = Utils.newArrayList();
		private final List<ServiceMethodParameter> parameters = Utils.newArrayList();
		
		ServiceMethod(Method m) {
			this.method = m;
			this.methodInfo = m.getAnnotation(MethodInfo.class);
			//
			final Annotation[] allAnnotations = method.getAnnotations();
			for (Annotation annotation : allAnnotations) {
				String handlerInAnnotation = annotation.annotationType().getAnnotation(Handler.class) == null?null:annotation.annotationType().getAnnotation(Handler.class).handler();
				ServiceHandler handler = (handlerInAnnotation == null?module.getAnnotationHandler(annotation.annotationType().getName()) : (ServiceHandler)module.getService(handlerInAnnotation));
				if (handler != null) {
					handlers.add(new AnnotationHandler(annotation, handler));
				}
			}
			// 加入默认的
			ServiceHandler defaultHandler = module.getAnnotationHandler("DEFAULT");
			if (defaultHandler != null) {
				handlers.add(0, new AnnotationHandler(null, defaultHandler));
			}
			//
			final Parameter[] ps = m.getParameters();
			for(Parameter p: ps) {
				this.parameters.add(new ServiceMethodParameter(p));
			}
		}

		public MethodInfo getMethodInfo() {
			return methodInfo;
		}

		public List<ServiceMethodParameter> getParameters() {
			return parameters;
		}
	}

	public class AnnotationHandler {
		final Annotation annotation;
		final ServiceHandler handler;
		AnnotationHandler(final Annotation annotation, final ServiceHandler handler) {
			this.annotation = annotation;
			this.handler = handler;
		}
	}
	
	public class ServiceMethodParameter {
		private final Class type;
		private final Parameter parameter;
		private final Param param;
		
		ServiceMethodParameter(Parameter parameter) {
			this.parameter = parameter;
			this.type = parameter.getType();
			this.param = parameter.getAnnotation(Param.class);
		}
		
		public Class getType() {
			return type;
		}
		
		public Parameter getParameter() {
			return parameter;
		}
		
		public Param getParam() {
			return param;
		}
		
		public String getTypeName() {
			if(this.isArray())
				return this.type.getComponentType().getSimpleName()+"[]";
			else
				return this.type.getSimpleName();
		}
		
		public boolean isArray() {
			return this.type.isArray();
		}
		
		public boolean isRequired() {
			return param == null? false: param.required();
		}
		
		public String getMax() {
			return param == null? null: param.max();
		}
		
		public String getMin() {
			return param == null? null: param.min();
		}
		
		public ConstantEnum[] getEnums() {
			if(param != null && param.enumClass() != ConstantEnum.class) {
				return param.enumClass().getEnumConstants();
			} else {
				return null;
			}
		}
		
		public int maxLength() {
			return param == null? 0: param.maxlength();
		}
		
		public String getFormat() {
			return param == null? null: param.format();
		}
		
		public String getParamName() {
			if(param == null) return null;
			return !StringUtils.isEmpty(param.alias()) ? param.alias() : param.value();
		}
		
		public String getDescription() {
			return param == null?null: param.desc();
		}
	}
}
