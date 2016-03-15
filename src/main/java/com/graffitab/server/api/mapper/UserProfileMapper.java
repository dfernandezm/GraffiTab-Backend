package com.graffitab.server.api.mapper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.user.UserProfileDto;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.user.UserService;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class UserProfileMapper extends CustomMapper<User, UserProfileDto> {

	@Resource
	private UserService userService;

	@Override
	public void mapAtoB(User a, UserProfileDto b, MappingContext context) {
		super.mapAtoB(a, b, context);

		b.setFollowersCount(0);
		b.setFollowingCount(0);
		b.setStreamablesCount(0);
	}
}
