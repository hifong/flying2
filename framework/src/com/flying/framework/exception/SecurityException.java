package com.flying.framework.exception;

import com.flying.framework.security.Principal;

public class SecurityException extends AppException {

	private static final long serialVersionUID = 1875385353212211630L;
	
	private final Principal principal;

	public SecurityException(Principal principal, String errorCode, String message) {
		super(errorCode, message);
		this.principal = principal;
	}

	public Principal getPrincipal() {
		return principal;
	}
}
