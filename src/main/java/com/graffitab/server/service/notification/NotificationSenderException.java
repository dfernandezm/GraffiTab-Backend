package com.graffitab.server.service.notification;

public class NotificationSenderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotificationSenderException(String msg) {
		super(msg);
	}

	public NotificationSenderException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
