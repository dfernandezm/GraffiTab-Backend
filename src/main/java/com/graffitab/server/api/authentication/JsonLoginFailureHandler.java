package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.graffitab.server.api.errors.LoginUserNotActiveException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;

// Rest-based Auth
public class JsonLoginFailureHandler implements AuthenticationFailureHandler {

	@Resource(name = "delegateJacksonHttpMessageConverter")
	private MappingJackson2HttpMessageConverter jsonConverter;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		JSONObject json = new JSONObject();
		String resultCode = HttpStatus.UNAUTHORIZED.name();

		String message = "";
		if (exception instanceof BadCredentialsException) {
			message = "Invalid username/password provided";
		} else if (exception instanceof LoginUserNotActiveException) {
			LoginUserNotActiveException loginUserNotActiveException = (LoginUserNotActiveException) exception;
			RestApiException restApiException = (RestApiException) loginUserNotActiveException.getCause();
			message = restApiException.getMessage();
			resultCode = restApiException.getResultCode().name();
		} else if (exception instanceof UsernameNotFoundException) {
			message = exception.getMessage();
			resultCode = ResultCode.USER_NOT_FOUND.name();
		} else {
			message = exception.getMessage();
		}

		json.put("resultCode", resultCode);
		json.put("resultMessage", message);

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		IOUtils.write(json.toString(), response.getOutputStream());
	}
}
