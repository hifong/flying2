package com.flying.framework.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.annotation.ServiceField;
import com.flying.framework.annotation.ServiceFieldParam;
import com.flying.framework.context.ServiceContext;
import com.flying.framework.data.Data;
import com.flying.framework.module.LocalModule;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ModelProxy<T extends Data> implements MethodInterceptor {
	private final static Logger logger = Logger.getLogger(ModelProxy.class);
	private final static Map<Class<?>, List<Field>> classFields = Utils.newHashMap();
	
	protected final LocalModule module;
	protected final Class<T> modelClass;

	private final Enhancer enhancer = new Enhancer();
	protected final T modelObject;
	
	@SuppressWarnings("unchecked")
	public ModelProxy(LocalModule module, Class<T> cls) {
		if(module == null) {
			module = ServiceContext.getContext() == null? null: ServiceContext.getContext().getModule();
		}
		this.module = module;
		this.modelClass = cls;

		if(module != null) {
			enhancer.setClassLoader(module.getClassLoader());
		}
		enhancer.setSuperclass(cls);
		enhancer.setCallback(this);
		
		modelObject = (T)enhancer.create();
		if(!classFields.containsKey(cls)) {
			synchronized(cls) {
				if(!classFields.containsKey(cls)) {
					Field fields[] = modelClass.getDeclaredFields();
					List<Field> fs = Utils.newArrayList();
					for(Field f: fields) {
						if(Modifier.isStatic(f.getModifiers()) || Modifier.isPublic(f.getModifiers())){
							continue;
						}
						fs.add(f);
					}
					classFields.put(cls, fs);
				}
			}
		}
	}
	
	public T getInstance() {
		return modelObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object intercept(Object service, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		//从Method的名字解析出属性
		final String methodName = method.getName();
		logger.debug("intercept in method:"+ methodName);
		if(methodName.startsWith("get") && (args == null || args.length == 0)) { //getter
			String fieldName = StringUtils.capitalize(methodName.substring(3));
			Field field = this.getFieldByFieldName(fieldName);
			if(field == null) {
				return methodProxy.invokeSuper(service, args);
			}
			Param param = field.getAnnotation(Param.class);
			if(param == null) {
				return methodProxy.invokeSuper(service, args);
			}
			ServiceField serviceField = field.getAnnotation(ServiceField.class);
			//
			logger.debug("getter -> field:" + field.getName() +" vs param:"+ param.value());
			if(modelObject.contains(param.value())) {
				return modelObject.get(param.value(), method.getReturnType());
			} else if(modelObject.contains(param.alias())) {
				return modelObject.get(param.alias(), method.getReturnType());
			} else if(modelObject.contains(field.getName())) {
				return modelObject.get(field.getName(), method.getReturnType());
			} else if(serviceField != null) { //load from service within lazy mode
				if(ServiceField.NONE.equals(serviceField.serviceId())) {
					return methodProxy.invokeSuper(service, args);
				}
				//
				final String moduleId = StringUtils.isEmpty(serviceField.moduleId()) ? this.module.getId(): serviceField.moduleId();
				final String serviceId = serviceField.serviceId();
				final Data params = new Data();
				for(ServiceFieldParam sfp: serviceField.params()) {
					Object value = null;
					if(sfp.value() != null) {
						if(sfp.value().startsWith("$")) {
							value = modelObject.get(sfp.value().substring(1));
						} else {
							value = sfp.value();
						}
					}
					if(value != null) {
						params.put(sfp.param(), value);
					} else {
						return null;
					}
					try {
						final String valueAttribute = serviceField.valueAttribute();
						Data fieldResultData = ServiceHelper.invoke(moduleId, serviceId, params);
						if("SELF".equalsIgnoreCase(valueAttribute)) {
							modelObject.put(param.value(), fieldResultData);
							return fieldResultData;
						} else {
							Object result = fieldResultData.get(valueAttribute);
							modelObject.put(param.value(), result);
							return result;
						}
					} catch (Exception e) {
						logger.error("【"+modelObject + "】ModelProxy getProperty for " + modelClass.getName() + "."+ field.getName() +" fail, reason is :"+e);
						throw e;
					}
					
				}
			}
		} else if(methodName.startsWith("set") && (args != null && args.length == 1)) { //setters
			String fieldName = StringUtils.capitalize(methodName.substring(3));
			Field field = this.getFieldByFieldName(fieldName);
			if(field == null) {
				return methodProxy.invokeSuper(service, args);
			}
			Param param = field.getAnnotation(Param.class);
			if(param == null) {
				return methodProxy.invokeSuper(service, args);
			}
			logger.debug("setter -> field:" + field.getName() +" vs param:"+ param.value());
			modelObject.put(param.value(), args[0]);
			return methodProxy.invokeSuper(service, args);
		} else if(methodName.equals("get") && args != null && args.length == 1) {
			String key = (String)args[0];
			if(modelObject.contains(key)) {
				return modelObject.getValues().get(key);
			} 
			Field field = this.getFieldByFieldName(key);
			if(field != null) {
				Param param = field.getAnnotation(Param.class);
				if(param != null && modelObject.contains(param.value())) {
					return modelObject.getValues().get(param.value());
				}
			} else {
				field = this.getFieldByParam(key);
				if(field != null) {
					if(modelObject.contains(field.getName())) {
						return modelObject.getValues().get(field.getName());
					} else {
						Param param = field.getAnnotation(Param.class);
						if(param != null && modelObject.contains(param.value())) {
							return modelObject.getValues().get(param.value());
						}
					}
				}
			}
		} else if(methodName.equals("put") && args != null && args.length == 2) {
			String key = (String)args[0];
			Object value = args[1];
			//
			Field field = this.getFieldByParam(key);
			if(field != null) {
				setProperty(modelObject, field.getName(), value);
				modelObject.put(key, value);
			} else {
				field = this.getFieldByFieldName(key);
				Param param = field == null? null: field.getAnnotation(Param.class);
				if(field != null) {
					setProperty(modelObject, field.getName(), value);
				}
				if(param != null) {
					return modelObject.put(param.value(), args[0]);
				}
			}
		} else if(methodName.equals("putAll") && args != null && args.length == 1 && args[0] instanceof Map) {
			Map<String, Object> values = (Map<String, Object>)args[0];
			for(Entry<String, Object> e: values.entrySet()) {
				String key = e.getKey();
				Object value = e.getValue();
				modelObject.put(key, value);
				//
				Field field = this.getFieldByParam(key);
				if(field != null) {
					setProperty(modelObject, field.getName(), value);
				}
			}
		} else if(methodName.equals("remove") && args != null && args.length == 1) {
			String key = (String)args[0];
			modelObject.remove(key);
			//
			Field field = this.getFieldByParam(key);
			if(field != null) {
				setProperty(modelObject, field.getName(), null);
			}
		}
		return methodProxy.invokeSuper(service, args);
	}
	
	private void setProperty(Object obj, String fieldName, Object value) {
		try {
			List<Field> fields = classFields.get(modelClass);
			
			boolean found = false;
			for(Field f: fields) {
				if(StringUtils.equals(fieldName, f.getName())) {
					found = true;
					break;
				}
			}
			if(found)
				BeanUtils.setProperty(modelObject, fieldName, value);
		} catch (Exception e) {
			logger.warn(String.format("set property for %s.%s=%s fail", obj, fieldName, value));
		}
	}
	
	private Field getFieldByFieldName(String fieldName) {
		List<Field> fields = classFields.get(modelClass);
		Optional<Field> f = fields.stream().filter(x -> x.getName().equalsIgnoreCase(fieldName)).findFirst();
		return f.isPresent()?f.get(): null;
	}
	
	private Field getFieldByParam(String paramValue) {
		List<Field> fields = classFields.get(modelClass);
		for(int i=0; i< fields.size() && fields != null; i++) {
			Field field = fields.get(i);
			Param param = field == null? null: field.getAnnotation(Param.class);
			if(param != null && StringUtils.equals(paramValue, param.value())) {
				return field;
			}
		}
		return null;
	}
}
