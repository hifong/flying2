package com.flying.common.annotation.handler;

import com.flying.framework.exception.AppException;

public class DaoException extends AppException {
	private static final long serialVersionUID = 2549580219417604830L;

	public DaoException(Throwable t, String message) {
		super(t, "1", message);
	}

	public DaoException(String message) {
		super("1", message);
	}

	@Override
	public String getMessage() {
		final String supermsg = super.getMessage();
		return "数据库错误，模块["+ moduleId +"]." + serviceId+"，错误原因：" + (supermsg == null?super.getCause().getMessage():supermsg);
	}
}
