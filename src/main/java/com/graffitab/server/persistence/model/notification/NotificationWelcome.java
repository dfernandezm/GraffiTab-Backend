package com.graffitab.server.persistence.model.notification;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "notification")
@DiscriminatorValue("WELCOME")
public class NotificationWelcome extends Notification {

	private static final long serialVersionUID = 1L;

	public NotificationWelcome() {
		super(NotificationType.WELCOME);
	}
}
