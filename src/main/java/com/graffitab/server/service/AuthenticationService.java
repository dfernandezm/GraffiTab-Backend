package com.graffitab.server.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

	public String generateBasicAuthToken(String username, String password) {
		String authenticationString = username + ":" + password;
		byte[] encodedBytes = Base64.encodeBase64(authenticationString.getBytes());
		String authenticationToken = new String(encodedBytes);
		return authenticationToken;
	}
}
