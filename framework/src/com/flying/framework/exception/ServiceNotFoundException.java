package com.flying.framework.exception;


@SuppressWarnings("serial")
public class ServiceNotFoundException extends ObjectNotFoundException {

	public ServiceNotFoundException(Throwable t, String serviceId) {
		super(t, "Service", serviceId);
	}

	public ServiceNotFoundException(String serviceId) {
		super("Service", serviceId);
	}
	
}
