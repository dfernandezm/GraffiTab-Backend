package com.graffitab.server.api.dto.user;

import lombok.Data;

@Data
public class CreateUserResult {

	private UserDto user;
	private String token;
}
