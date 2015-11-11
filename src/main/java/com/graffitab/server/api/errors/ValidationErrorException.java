package com.graffitab.server.api.errors;

public class ValidationErrorException extends RestApiException {

	private static final long serialVersionUID = 1L;
	
	public ValidationErrorException(String message) {
		super(ResultCode.BAD_REQUEST, message);
	}
}
