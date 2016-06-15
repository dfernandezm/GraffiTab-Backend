package com.graffitab.server.api.dto.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.dto.externalprovider.ExternalProviderDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullUserDto extends UserDto {

	private Integer followersCount;
	private Integer followingCount;
	private Long streamablesCount;

	@JsonProperty(value = "linkedAccounts")
	private List<ExternalProviderDto> externalProviders;
}
