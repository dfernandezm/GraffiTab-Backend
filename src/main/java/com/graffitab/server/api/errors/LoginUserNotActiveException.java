package com.graffitab.server.api.errors;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

public class LoginUserNotActiveException extends InternalAuthenticationServiceException{
	private static final long serialVersionUID = 1L;

	public LoginUserNotActiveException(String message, Throwable cause) {
		super(message, cause);
	}
}
