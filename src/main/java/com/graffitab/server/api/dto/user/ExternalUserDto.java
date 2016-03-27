package com.graffitab.server.api.dto.user;

import lombok.Data;

import com.graffitab.server.api.dto.user.ExternalProviderDto.ExternalProviderType;

@Data
public class ExternalUserDto {

	private UserDto user;
	private String externalId;
	private String accessToken;
	private ExternalProviderType externalProviderType;
}
