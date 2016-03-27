package com.graffitab.server.api.mapper.notification;

import javax.annotation.Resource;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.notification.NotificationDto;
import com.graffitab.server.persistence.model.notification.NotificationFollow;
import com.graffitab.server.service.user.UserService;

@Component
public class NotificationFollowMapper extends CustomMapper<NotificationFollow, NotificationDto> {

	@Resource
	private UserService userService;

	@Override
	public void mapAtoB(NotificationFollow a, NotificationDto b, MappingContext context) {
		super.mapAtoB(a, b, context);

		b.getFollower().setFollowedByCurrentUser(userService.getCurrentUser().isFollowing(a.getFollower()));
	}
}
