package com.graffitab.server.persistence.model.notification;

import com.graffitab.server.persistence.model.User;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NotificationFollow extends Notification {

	private static final long serialVersionUID = 1L;

	private User follower;
}
