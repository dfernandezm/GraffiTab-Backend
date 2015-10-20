package com.graffitab.server.api.dto.user;

import java.util.List;

import com.graffitab.server.persistence.model.User;

public class ListUsersResult {
	
	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
