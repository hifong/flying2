package com.flying.common.filter;

import com.flying.common.util.Codes;
import com.flying.framework.exception.SecurityException;
import com.flying.framework.security.Principal;

@SuppressWarnings("serial")
public class URIAuthenticationException extends SecurityException {
	private final String uri;
	
	public URIAuthenticationException(Principal principal, String message, String uri) {
		super(principal, message, String.valueOf(Codes.AUTH_FAIL));
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}
