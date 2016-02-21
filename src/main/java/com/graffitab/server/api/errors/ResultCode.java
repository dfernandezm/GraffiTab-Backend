package com.graffitab.server.api.errors;

public enum ResultCode {

	OK(200),
	NOT_FOUND(404),
	TOKEN_EXPIRED(406),
	USER_NOT_FOUND(404),
	BAD_REQUEST(400),
	USER_NOT_LOGGED_IN(401),
	USER_NOT_IN_EXPECTED_STATE(406),
	INCORRECT_PASSWORD(403),
	GENERAL_ERROR(500);

	private Integer statusCode;

	public Integer getStatusCode() {
		return statusCode;
	}

	public String getStatusName() {
		return this.name();
	}

	private ResultCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
}
