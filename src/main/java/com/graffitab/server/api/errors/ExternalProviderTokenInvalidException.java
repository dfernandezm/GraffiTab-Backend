package com.graffitab.server.api.errors;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class ExternalProviderTokenInvalidException extends InternalAuthenticationServiceException{
	private static final long serialVersionUID = 1L;

	public ExternalProviderTokenInvalidException(String message, Throwable cause) {
		super(message, cause);
	}
}
