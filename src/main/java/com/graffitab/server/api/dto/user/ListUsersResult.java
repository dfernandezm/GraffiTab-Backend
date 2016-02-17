package com.graffitab.server.api.dto.user;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListUsersResult {

	private List<UserDto> users;
	private Integer total;
	private Integer offset;
	private Integer pageSize;
}
