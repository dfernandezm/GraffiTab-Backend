package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

// Rest-based Auth
public class JsonLoginFailureHandler implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {
		
		JSONObject json = new JSONObject();
		json.put("resultCode", HttpStatus.UNAUTHORIZED.value());
		
		String message = "";
		if (exception instanceof BadCredentialsException) {
			message = "Invalid username/password provided";
		} else {
			message = exception.getMessage();
		}
		
		json.put("resultMessage", message);
		
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		IOUtils.write(json.toString(), response.getOutputStream());
		response.flushBuffer();
	}
}
