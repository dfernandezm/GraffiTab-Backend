package com.graffitab.server.persistence.model.notification;

import org.joda.time.LocalDateTime;

import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.persistence.model.User;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode
public class Notification implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	public enum NotificationType {COMMENT, FOLLOW, LIKE, MENTION, WELCOME}

	private Long id;
	private User user;
	private Boolean isRead;
	private NotificationType notificationType;
	private LocalDateTime date;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
}
