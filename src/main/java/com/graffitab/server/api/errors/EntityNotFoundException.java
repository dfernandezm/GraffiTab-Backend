package com.graffitab.server.api.errors;

public class EntityNotFoundException extends RestApiException {

	private static final long serialVersionUID = 1L;

	public EntityNotFoundException(ResultCode resultCode, String message) {
		super(resultCode, message);
	}
}
