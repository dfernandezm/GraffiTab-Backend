package com.graffitab.server.api.mapper.notification;

import javax.annotation.Resource;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.notification.NotificationDto;
import com.graffitab.server.persistence.model.notification.NotificationComment;
import com.graffitab.server.service.user.UserService;

@Component
public class NotificationCommentMapper extends CustomMapper<NotificationComment, NotificationDto> {

	@Resource
	private UserService userService;

	@Override
	public void mapAtoB(NotificationComment a, NotificationDto b, MappingContext context) {
		super.mapAtoB(a, b, context);

		b.getCommenter().setFollowedByCurrentUser(userService.getCurrentUser().isFollowing(a.getCommenter()));
	}
}
