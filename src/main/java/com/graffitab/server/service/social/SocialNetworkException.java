package com.graffitab.server.service.social;

public class SocialNetworkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SocialNetworkException(String msg) {
		super(msg);
	}

	public SocialNetworkException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
