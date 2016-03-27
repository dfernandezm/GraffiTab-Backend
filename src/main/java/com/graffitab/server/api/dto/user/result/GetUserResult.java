package com.graffitab.server.api.dto.user.result;

import lombok.Data;

import com.graffitab.server.api.dto.user.UserDto;

@Data
public class GetUserResult {

	private UserDto user;
}
