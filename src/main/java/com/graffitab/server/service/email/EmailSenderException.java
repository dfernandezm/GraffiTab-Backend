package com.graffitab.server.service.email;

public class EmailSenderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailSenderException(String msg) {
		super(msg);
	}

	public EmailSenderException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
