package com.flying.framework.exception;

import org.apache.commons.lang3.StringUtils;

import com.flying.framework.application.Application;
import com.flying.framework.context.ServiceContext;

/**
 * @author wanghaifeng
 *
 */
@SuppressWarnings("serial")
public class AppException extends RuntimeException {
	protected String errorCode = ErrorCode.INTERNAL_ERROR;
	protected String moduleId;
	protected String serviceId;
	protected String uuid;
	
	private void init () {
		ServiceContext sc = ServiceContext.getContext();
		if(sc != null && sc.getModule() != null) {
			this.moduleId = sc.getModule().getId();
			this.serviceId = sc.getCurrentServiceInfo() != null?sc.getCurrentServiceInfo().serviceId: null;
			this.uuid = sc.getUuid();
		}
	}
	
	public AppException(String errorCode) {
		super();
		this.init();
		this.errorCode = errorCode;
	}
	
	public AppException(String errorCode, String message) {
		super(message);
		this.init();
		this.errorCode = errorCode;
	}
	
	public AppException(Throwable t) {
		super(t);
		this.init();
		if(t instanceof AppException) {
			this.errorCode = ((AppException)t).getErrorCode();
		}
	}
	
	public AppException(Throwable t, String errorCode) {
		super(t);
		this.init();
		this.errorCode = errorCode;
	}
	
	public AppException(Throwable t, String errorCode, String message) {
		super(message, t);
		this.init();
		this.errorCode = errorCode;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public String getModuleId() {
		return moduleId;
	}

	public String getServiceId() {
		return serviceId;
	}
	
	public String getUuid() {
		return this.uuid;
	}
	
	public String getMessage() {
		String msg = super.getMessage();
		if(StringUtils.isEmpty(msg) && !StringUtils.isEmpty(moduleId)) {
			msg = Application.getInstance().getModules().getLocalModule(moduleId).getModuleConfig().getConfig("msg." + this.errorCode);
		}
		if(StringUtils.isEmpty(msg)) {
			msg = "系统错误!" +"错误码：" + this.errorCode;
		}
		return msg;
	}
}
