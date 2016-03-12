package com.graffitab.server.api.dto.user.result;

import com.graffitab.server.api.dto.user.UserProfileDto;

import lombok.Data;

@Data
public class GetUserProfileResult {

	private UserProfileDto user;
}
