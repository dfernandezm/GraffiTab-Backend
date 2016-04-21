package com.graffitab.server.api.mapper.user;

import javax.annotation.Resource;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.user.FullUserDto;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.service.user.UserService;

@Component
public class FullUserMapper extends CustomMapper<User, FullUserDto> {

	@Resource
	private UserService userService;

	@Override
	public void mapAtoB(User user, FullUserDto userDto, MappingContext context) {
		userService.processStats(user, userDto);
	}
}
