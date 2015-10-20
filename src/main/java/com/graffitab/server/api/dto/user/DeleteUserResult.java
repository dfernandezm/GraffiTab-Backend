package com.graffitab.server.api.dto.user;

import com.graffitab.server.persistence.model.User;

public class DeleteUserResult {

	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
}
