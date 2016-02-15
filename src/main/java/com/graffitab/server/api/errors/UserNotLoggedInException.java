package com.graffitab.server.api.errors;

public class UserNotLoggedInException extends RestApiException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public UserNotLoggedInException(String msg) {
		super(ResultCode.USER_NOT_LOGGED_IN, msg);
	}


}
