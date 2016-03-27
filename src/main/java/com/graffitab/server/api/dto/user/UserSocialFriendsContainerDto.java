package com.graffitab.server.api.dto.user;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonInclude(Include.NON_NULL)
public class UserSocialFriendsContainerDto {

	@JsonProperty("type")
	private String externalProviderType;

	private List<UserDto> users;
	private Integer resultsCount;
	private Integer offset;
	private Integer limit;
}
