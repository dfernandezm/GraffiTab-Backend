package com.graffitab.server.api.dto.user.result;

import lombok.Data;

import com.graffitab.server.api.dto.user.FullUserDto;

@Data
public class GetFullUserResult {

	private FullUserDto user;
}
