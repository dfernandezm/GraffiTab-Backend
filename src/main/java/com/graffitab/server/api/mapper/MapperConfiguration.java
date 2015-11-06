package com.graffitab.server.api.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.persistence.model.User;

@Component
public class MapperConfiguration {

	
	private static MapperFactory mapperFactory;
	
	
	public static MapperFactory getMapperFactory() {
	
		if (mapperFactory == null) {
			mapperFactory = new DefaultMapperFactory.Builder().build();
			mapperFactory.classMap(User.class, UserDto.class)
			   .byDefault()
			   .register();
			
		}
		
		return mapperFactory;
	}
		
	public void mapUser(UserDto userDto, User user) {
		MapperFactory mapperFactory = getMapperFactory();
		MapperFacade mapper = mapperFactory.getMapperFacade();
		mapper.map(userDto, user);
	}
	
}
