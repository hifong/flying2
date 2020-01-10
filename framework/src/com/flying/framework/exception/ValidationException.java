package com.flying.framework.exception;

import java.util.List;
import java.util.Map;

import com.flying.common.util.Codes;
import com.flying.framework.application.Application;

public class ValidationException extends AppException {
	private static final long serialVersionUID = -2546656607293817934L;
	
	private final String method;
	private final Map<String, List<ValidationError>> errors;
	
	public ValidationException(String moduleId, String serviceId, String method, Map<String, List<ValidationError>> errors) {
		super(String.valueOf(Codes.INVALID_PARAM), moduleId);
		this.serviceId = serviceId;
		this.method = method;
		this.errors = errors;
		
	}

	public String getMethod() {
		return method;
	}

	public Map<String, List<ValidationError>> getErrors() {
		return errors;
	}
	
	public String toString() {
		return moduleId +" " + serviceId + ":" + method +"\n" + errors;
	}
	
	public String getMessage() {
		final String msg = Application.getInstance().getModules().getLocalModule(moduleId).getModuleConfig().getConfig("msg."+this.errorCode);
		StringBuffer sb = new StringBuffer(msg == null?"":msg);
		for(String f: this.errors.keySet()) {
			List<ValidationError> l = this.errors.get(f);
			for(ValidationError e: l) {
				sb.append(e.toString()).append(";");
			}
		}
		return sb.toString();
	}
}
