package com.graffitab.server.config.web;

import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;

import lombok.Getter;

public class MissingJsonPropertyException extends RestApiException {

	private static final long serialVersionUID = 1L;

	@Getter
	private String requestedJsonProperty;

	public MissingJsonPropertyException(String msg, String requestedJsonProperty) {
		super(msg);
		this.requestedJsonProperty = requestedJsonProperty;
		super.setResultCode(ResultCode.MISSING_ARGUMENT);
	}
}
