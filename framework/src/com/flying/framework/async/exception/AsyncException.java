package com.flying.framework.async.exception;

import com.flying.framework.exception.AppException;

/**
 * @author wanghaifeng
 *
 */
public class AsyncException extends AppException {
	private static final long serialVersionUID = -4637031592601941595L;
	public AsyncException(Throwable t, String moduleId, String serviceId) {
		super(t, moduleId, serviceId);
	}
}
