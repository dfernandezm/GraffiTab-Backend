package com.graffitab.server.api.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullUserDto extends UserDto {

	private Integer followersCount;
	private Integer followingCount;
	private Integer streamablesCount;
}
