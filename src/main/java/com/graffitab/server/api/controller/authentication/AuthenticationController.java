package com.graffitab.server.api.controller.authentication;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.graffitab.server.api.dto.AuthenticationTokenResult;
import com.graffitab.server.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	@Resource
	private AuthenticationService authenticationService;

	@RequestMapping(value = "/token/basic", method = RequestMethod.GET)
	public AuthenticationTokenResult submitFeedback(
			@RequestParam(value="username", required = true) String username,
			@RequestParam(value="password", required = true) String password) {
		AuthenticationTokenResult authenticationTokenResult = new AuthenticationTokenResult();
		String token = authenticationService.generateBasicAuthToken(username, password);
		authenticationTokenResult.setToken(token);
		return authenticationTokenResult;
	}
}
