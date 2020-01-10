package com.flying.framework.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.flying.framework.data.Data;
import com.flying.framework.data.DataUtils;
import com.flying.framework.module.LocalModule;
import com.flying.framework.service.ServiceDescriptor.AnnotationHandler;
import com.flying.framework.service.ServiceDescriptor.ServiceMethodParameter;

@SuppressWarnings("rawtypes")
public class ServiceHandlerContext {
	
	private final ServiceProxy service;
	private final Method method;
	private final String methodName;
	private final ServiceHandler methodHandler;
	private final Class<? extends Data> returnType;
	
	private int curHandlerIndex;
	
	@SuppressWarnings("unchecked")
	public ServiceHandlerContext(ServiceProxy service, Method method, ServiceHandler handler) {
		this.service = service;
		this.method = method;
		this.methodName = method.getName();
		this.methodHandler = handler;
		this.returnType = (Class<? extends Data>)method.getReturnType();
	}
	
	@SuppressWarnings("unchecked")
	public Data doChain(Data request) throws Exception {
		List<AnnotationHandler> annotationHandlers = this.service.getServiceDescriptor().getMethodAnnotationHandlers(method);
		curHandlerIndex ++;
		if(curHandlerIndex <= annotationHandlers.size() + 1) {
			ServiceHandler handler;
			Annotation annotation;
			if(curHandlerIndex <= annotationHandlers.size()) {
				handler = annotationHandlers.get(curHandlerIndex - 1).handler;
				annotation = annotationHandlers.get(curHandlerIndex - 1).annotation;
			} else {
				handler = this.methodHandler;
				annotation = null;
			}
			
			if(request != null) {
				for(Iterator<String> keys = request.keys().iterator(); keys.hasNext(); ) {
					String k = keys.next();
					if(k.startsWith("arg")) {
						request.merge(DataUtils.serialize(request.get(k)), false);
					}
				}
			}
			
			return handler.handle(annotation, request, this);
		}
		return null;
	}
	
	public LocalModule getModule() {
		return this.service.module;
	}
	
	public String getServiceId() {
		return this.service.serviceConfig.getId();
	}

	public String getMethodName() {
		return methodName;
	}

	public List<ServiceMethodParameter> getMethodParams() {
		return this.service.getServiceDescriptor().getMethodParameters(method);
	}

	public Class<? extends Data> getReturnType() {
		return returnType;
	}
}
