package com.graffitab.server.api.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class CustomFailureBasicAuthFilter extends BasicAuthenticationFilter {

	public CustomFailureBasicAuthFilter(
			AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
		super(authenticationManager, authenticationEntryPoint);
	}
}
