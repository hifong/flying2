package com.flying.framework.exception;

@SuppressWarnings("serial")
public class ServiceException extends AppException {

	public ServiceException(Throwable t) {
		super(t);
	}

	public ServiceException(String errorCode, String message) {
		super(errorCode, message);
	}

}
