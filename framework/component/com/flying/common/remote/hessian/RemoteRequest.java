package com.flying.common.remote.hessian;

import java.io.Serializable;

import com.flying.framework.data.Data;
import com.flying.framework.security.Principal;

/**
 * @author wanghaifeng
 * 
 */
public class RemoteRequest implements Serializable {
	private static final long serialVersionUID = -4319119127602853305L;
	private final String uuid;
	private final Data request;
	private final Principal principal;
	private Data response;
	private final String fromModuleId;
	private final String toModuleId;
	private Throwable exception;

	public RemoteRequest(Data request, String uuid, String fromModuleId, String toModuleId, Principal principal) {
		this.uuid = uuid;
		this.request = request;
		this.fromModuleId = fromModuleId;
		this.toModuleId = toModuleId;
		this.principal = principal;
	}
	
	public String getUuid() {
		return uuid;
	}

	public String getFromModuleId() {
		return fromModuleId;
	}

	public String getToModuleId() {
		return toModuleId;
	}

	public Principal getPrincipal() {
		return principal;
	}

	public Data getRequest() {
		return request;
	}

	public Data getResponse() {
		return response;
	}

	public void setResponse(Data response) {
		this.response = response;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception;
	}
}
