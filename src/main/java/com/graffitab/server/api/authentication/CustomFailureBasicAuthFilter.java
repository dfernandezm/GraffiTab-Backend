package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class CustomFailureBasicAuthFilter extends BasicAuthenticationFilter {

	public CustomFailureBasicAuthFilter(
			AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}


	protected void onUnsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
			throws IOException {
		
		JSONObject json = new JSONObject();
		json.put("resultCode", HttpStatus.UNAUTHORIZED.value());
		
		String message = "";
		if (failed instanceof BadCredentialsException) {
			message = "Invalid username/password provided";
		} else {
			message = failed.getMessage();
		}
		
		json.put("resultMessage", message);
		
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		IOUtils.write(json.toString(), response.getOutputStream());
		response.flushBuffer();
		
	}
	
}
