package com.graffitab.server.api.dto.user.result;

import com.graffitab.server.api.dto.user.UserDto;

import lombok.Data;

@Data
public class CreateUserResult {

	private UserDto user;
	private String token;
}
