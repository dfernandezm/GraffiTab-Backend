package com.graffitab.server.api.errors;

public class ValidationErrorException extends RestApiException {

	private static final long serialVersionUID = 1L;

	public ValidationErrorException(ResultCode code, String message) {
		super(code, message);
	}
}
