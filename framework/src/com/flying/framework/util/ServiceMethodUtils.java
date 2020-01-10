package com.flying.framework.util;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.flying.common.util.ClassUtils;
import com.flying.framework.annotation.Param;
import com.flying.framework.exception.ServiceNotFoundException;
import com.flying.framework.module.LocalModule;

public abstract class ServiceMethodUtils {

	public static MethodInfo getMethodInfo(LocalModule module, String serviceId, String methodName) 
			throws NoSuchMethodException, SecurityException {
		
		final String key = "METHOD."+serviceId+":"+methodName;
		if(module.containsVariable(key)) 
			return module.getVariable(key);
		
		final Object service = module.getService(serviceId);
		final Method[] methods = service.getClass().getDeclaredMethods();
		Method method = null;
		for(Method m: methods){
			if(m.getName().equals(methodName)) {
				method = m;
				break;
			}
		}
		if(method == null) throw new ServiceNotFoundException(serviceId+":"+methodName);
		
		//
		@SuppressWarnings("rawtypes")
		Class[] argTypes = method.getParameterTypes();
		@SuppressWarnings("rawtypes")
		Class superclass = service.getClass().getSuperclass();
		if(service.getClass().getName().indexOf("$$EnhancerByCGLIB$$") <0 ) superclass = null;
		@SuppressWarnings("unchecked")
		Method supermethod = superclass == null? null: superclass.getMethod(methodName, argTypes);
		//
		final Parameter[] parameters = supermethod == null? method.getParameters(): supermethod.getParameters();
		final Param[] params = new Param[parameters.length];
		for(int i=0; i < parameters.length; i++) {
			params[i] = parameters[i].getAnnotation(Param.class);
		}
		
		MethodInfo methodInfo = new MethodInfo(method, parameters, params);
		module.setVariable(key, methodInfo);
		//
		return methodInfo;
	}
	
	public static class MethodInfo {
		final public Method method;
		final public Parameter[] parameters;
		final public Param[] params;
		
		MethodInfo(Method method, Parameter[] parameters, Param[] params) {
			this.method = method;
			this.parameters = parameters;
			this.params = params;
		}
	}
}
