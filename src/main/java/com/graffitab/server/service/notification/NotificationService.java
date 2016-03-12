package com.graffitab.server.service.notification;

import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.api.dto.ListItemsResult;
import com.graffitab.server.api.dto.notification.NotificationDto;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.notification.Notification;
import com.graffitab.server.persistence.model.notification.NotificationFollow;
import com.graffitab.server.persistence.model.notification.NotificationWelcome;
import com.graffitab.server.service.paging.PagingService;
import com.graffitab.server.service.paging.PagingServiceQueryProvider;
import com.graffitab.server.service.user.UserService;

@Service
public class NotificationService {

	private static final Logger log = LogManager.getLogger();

	@Resource
	private UserService userService;

	@Resource
	private PagingService pagingService;

	@Resource
	private HibernateDaoImpl<Notification, Long> notificationDao;

	@Resource
	private NotificationSenderService notificationSenderService;

	@Resource
	private OrikaMapper mapper;

	@Transactional
	public ListItemsResult<NotificationDto> getNotificationsResult(Integer offset, Integer count) {
		User currentUser = userService.getCurrentUser();

		return pagingService.getPagedItemsResult(Notification.class, NotificationDto.class, offset, count, new PagingServiceQueryProvider<Notification>() {

			@Override
			public Query getItemSearchQuery() {
				Query query = notificationDao.createQuery("select n from User u " + "join u.notifications"
						+ " n where u = :currentUser");
				query.setParameter("currentUser", currentUser);

				return query;
			}

			@Override
			public List<Notification> getAugmentedItemsList(List<Notification> items) {
				// Check if the current user is following user u from the list.
				for (Notification notification : items) {
					if (notification instanceof NotificationFollow) {
						User follower = ((NotificationFollow) notification).getFollower();
						follower.setFollowedByCurrentUser(currentUser.isFollowing(follower));
					}
				}

				return items;
			}
		});
	}

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
