package com.graffitab.server.api.errors;

import lombok.Data;

@Data
public class RestApiResult {

	private ResultCode resultCode;
	private String resultMessage;

	public RestApiResult() {
		this.resultCode = ResultCode.OK;
	}

	public RestApiResult(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
}
