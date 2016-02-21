package com.graffitab.server.api.errors;


public class RestApiException extends RuntimeException {

	/**
	 *
	 */
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

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
}
