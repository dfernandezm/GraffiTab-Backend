package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.graffitab.server.api.errors.ResultCode;

public class CommonAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {

		JSONObject json = new JSONObject();
		json.put("resultCode", ResultCode.USER_NOT_LOGGED_IN.name());

		String message = "";
		if (authException instanceof BadCredentialsException) {
			message = "Invalid username/password provided";
		} else if (authException instanceof AuthenticationCredentialsNotFoundException){
			message = "Not authenticated";
		} else {
			message = authException.getMessage();
		}

		json.put("resultMessage", message);

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		IOUtils.write(json.toString(), response.getOutputStream());
	}

}
