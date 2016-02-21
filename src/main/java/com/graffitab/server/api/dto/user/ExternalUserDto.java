package com.graffitab.server.api.dto.user;

import com.graffitab.server.api.dto.user.ExternalProviderDto.ExternalProviderType;

import lombok.Data;

@Data
public class ExternalUserDto extends UserDto {

	private String externalId;
	private String accessToken;
	private ExternalProviderType externalProviderType;
}
