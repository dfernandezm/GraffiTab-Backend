package com.graffitab.server.api.errors;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice(annotations = RestController.class)
public class GeneralErrorHandlingAdvice {

	private static Logger LOG = LogManager.getLogger();

	@ExceptionHandler(value = RestApiException.class)
    @ResponseBody
    public ResponseEntity<RestApiResult> apiException(RestApiException restApiException, WebRequest request) {
    	RestApiResult errorResult = new RestApiResult();
    	errorResult.setResultCode(restApiException.getResultCode());
    	errorResult.setResultMessage(restApiException.getMessage());
    	return new ResponseEntity<RestApiResult>(errorResult, new HttpHeaders(), HttpStatus.valueOf(restApiException.getResultCode().getStatusCode()));
    }

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestApiResult generalException(Throwable throwable, WebRequest request) {

    	RestApiResult errorResult = new RestApiResult();

    	if (throwable instanceof MethodArgumentTypeMismatchException) {
    		errorResult.setResultCode(ResultCode.BAD_REQUEST);
    		errorResult.setResultMessage("The argument passed to the endpoint is not of the expected type");
    	} else {
    		errorResult.setResultCode(ResultCode.GENERAL_ERROR);
    		errorResult.setResultMessage(throwable.getMessage());
    	}

    	LOG.error("Unexpected error occurred", throwable);
    	return errorResult;
    }
}