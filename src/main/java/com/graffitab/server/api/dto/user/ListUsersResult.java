package com.graffitab.server.api.dto.user;

import com.graffitab.server.persistence.model.PagedList;
import com.graffitab.server.persistence.model.User;

public class ListUsersResult {
	
	private PagedList<User> users;

	public PagedList<User> getUsers() {
		return users;
	}

	public void setUsers(PagedList<User> users) {
		this.users = users;
	}

}
