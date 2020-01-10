package com.flying.framework.exception;

@SuppressWarnings("serial")
public class ServiceAlreadyDefinedException extends AppException {
	private final String serviceId;
	private final String serviceClassOne;
	private final String serviceClassOther;

	public ServiceAlreadyDefinedException(String moduleId, String serviceId, String serviceClassOne, String serviceClassOther) {
		super("1", moduleId);
		this.serviceId = serviceId;
		this.serviceClassOne = serviceClassOne;
		this.serviceClassOther = serviceClassOther;
	}

	public String getServiceClassOne() {
		return serviceClassOne;
	}

	public String getServiceClassOther() {
		return serviceClassOther;
	}

	public String getMessage() {
		return "Service \"" + serviceId +"\" in Module\""+moduleId+"\" defined twice between [" + serviceClassOne +"] and [" + serviceClassOther +"]";
	}
}
