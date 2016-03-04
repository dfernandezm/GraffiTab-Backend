package com.graffitab.server.service.notification;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.notification.Notification;
import com.graffitab.server.persistence.model.notification.NotificationFollow;
import com.graffitab.server.persistence.model.notification.NotificationWelcome;
import com.graffitab.server.service.UserService;

@Service
public class NotificationService {

	private static final Logger log = LogManager.getLogger();

	@Resource
	private UserService userService;

	@Resource
	private HibernateDaoImpl<Notification, Long> notificationDao;

	@Resource
	private NotificationSenderService notificationSenderService;

	@Transactional
	public void addWelcomeNotification(User user) {
		Notification notification = new NotificationWelcome();
		user.getNotifications().add(notification);

		sendNotificationAsync(user, notification);
	}

	@Transactional
	public void addFollowNotification(User user, User follower) {
		Notification notification = new NotificationFollow(follower);
		user.getNotifications().add(notification);

		sendNotificationAsync(user, notification);
	}

	private void sendNotificationAsync(User receiver, Notification notification) {
		log.debug("About to send push notification to user " + receiver.getUsername());
		try {
			notificationSenderService.sendNotification(receiver, notification);
		} catch (Throwable t) {
			log.error("Error sending push notification", t);
		}
	}
}
