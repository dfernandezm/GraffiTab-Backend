package com.graffitab.server.api.dto.user;

import java.util.List;

import com.graffitab.server.persistence.model.User;

public class ListUsersResult {
	
	private List<UserDto> users;

	public List<UserDto> getUsers() {
		return users;
	}

	public void setUsers(List<UserDto> users) {
		this.users = users;
	}

}
