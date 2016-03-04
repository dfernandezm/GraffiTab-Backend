package com.graffitab.server.service.notification;

import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.notification.Notification;

public interface NotificationSenderService {

	void sendNotification(User user, Notification notification);
}
