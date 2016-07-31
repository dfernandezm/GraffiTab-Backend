package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.graffitab.server.api.errors.ResultCode;

public class JsonAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request,
			HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException,
			ServletException {


		JSONObject json = new JSONObject();
		json.put("resultCode", ResultCode.FORBIDDEN.name());

		String message = accessDeniedException.getMessage();

		json.put("resultMessage", message);

		response.setContentType("application/json");
		IOUtils.write(json.toString(), response.getOutputStream());
		response.setStatus(HttpStatus.FORBIDDEN.value());
	}

}
