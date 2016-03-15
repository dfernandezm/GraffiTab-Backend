package com.graffitab.server.api.errors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ResultCode resultCode;

	public RestApiException(String message) {
		super(message);
		this.resultCode = ResultCode.GENERAL_ERROR;
	}

	public RestApiException(ResultCode resultCode, String message) {
		super(message);
		this.resultCode = resultCode;
	}

	public RestApiException(ResultCode resultCode, String message, Throwable cause) {
		super(message, cause);
		this.resultCode = resultCode;
	}
}
