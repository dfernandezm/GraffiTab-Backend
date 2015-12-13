package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JsonLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private JSONObject json;
	
	public JsonLoginAuthenticationFilter() {
		super();
	}
	
	private void getPayload(HttpServletRequest request) {
		try {
			
			if (request.getContentType().equals("application/json")) {
				String payload = IOUtils.toString(request.getInputStream());
				if (payload.length() > 0) {
					JSONObject json = new JSONObject(payload);
					this.json = json;
				}
			}		
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected String obtainUsername(HttpServletRequest request) {
		getPayload(request);
		return json.getString("username");
	}
	
	@Override
	protected String obtainPassword(HttpServletRequest request) {
		return json.getString("password");
	}
}
