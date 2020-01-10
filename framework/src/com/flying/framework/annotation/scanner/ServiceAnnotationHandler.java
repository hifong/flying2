package com.flying.framework.annotation.scanner;

import com.flying.framework.annotation.Service;
import com.flying.framework.annotation.service.RequestType;
import com.flying.framework.module.LocalModule;

public class ServiceAnnotationHandler implements RegisteredAnnotationHandler<Service> {

	@Override
	public void handle(LocalModule module, Service annotation, Class<?> cls) {
		module.registerServiceClass(annotation, cls);
		RequestType rt = cls.getAnnotation(RequestType.class);
		if(rt != null && rt.value() != null) {
			for(String requestType: rt.value()) {
				module.registerRequestType(requestType, annotation.value());
			}
		}
	}

}
