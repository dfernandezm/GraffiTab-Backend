package com.graffitab.server.api.errors;

public class RestApiResult {

	private ResultCode resultCode;
	private String resultMessage;
	
	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	public RestApiResult() {
		this.resultCode = ResultCode.OK;
	}
	
	public RestApiResult(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	public ResultCode getResultCode() {
		return resultCode;
	}

	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
}
