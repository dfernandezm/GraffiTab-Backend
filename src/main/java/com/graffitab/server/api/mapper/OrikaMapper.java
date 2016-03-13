package com.graffitab.server.api.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.asset.AssetDto;
import com.graffitab.server.api.dto.notification.NotificationDto;
import com.graffitab.server.api.dto.streamable.StreamableDto;
import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.dto.user.UserProfileDto;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.notification.NotificationFollow;
import com.graffitab.server.persistence.model.notification.NotificationWelcome;
import com.graffitab.server.persistence.model.streamable.StreamableGraffiti;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Component
public class OrikaMapper {

	private static MapperFactory mapperFactory;
	private static MapperFacade mapperFacade;
	private static OrikaMapper instance;

	private static MapperFactory getMapperFactory() {

		if (mapperFactory == null) {
			mapperFactory = new DefaultMapperFactory.Builder().build();
			registerMappings();
		}

		return mapperFactory;
	}

	private static void registerMappings() {
		// By default there automatic mapping is enabled, not need to register every single class
		// Map user DTOs.
		mapperFactory.classMap(User.class, UserDto.class)
			.byDefault()
		    .register();

		mapperFactory.classMap(User.class, UserProfileDto.class)
			.use(User.class, UserDto.class)
			.byDefault()
		    .register();

		// Map asset DTOs.
		mapperFactory.classMap(Asset.class, AssetDto.class)
		.byDefault()
		.register();

		// Map notification DTOs.
		mapperFactory.classMap(NotificationWelcome.class, NotificationDto.class)
		.byDefault()
	    .register();

		mapperFactory.classMap(NotificationFollow.class, NotificationDto.class)
		.byDefault()
	    .register();

		// Map notification DTOs.
		mapperFactory.classMap(StreamableGraffiti.class, StreamableDto.class)
		.byDefault()
	    .register();
	}

	private static MapperFacade getMapperFacade() {

		if (mapperFacade == null) {
			mapperFacade = getMapperFactory().getMapperFacade();
		}

		return mapperFacade;
	}

	public <T,K> T map(K source, Class<T> destinationClass) {
	   return getMapperFacade().map(source, destinationClass);
	}

	public <T,K> void map(T source, K destination) {
		getMapperFacade().map(source, destination);
	}

	public <T> List<T> mapList(List<?> sourceList, Class<T> destinationListElementClass) {

		List<T> mappedList = new ArrayList<>(sourceList.size());
		MapperFacade mapperFacade = getMapperFacade();

		for (Object sourceElement : sourceList) {
			mappedList.add(mapperFacade.map(sourceElement, destinationListElementClass));
		}

		return mappedList;
	}

	public static OrikaMapper get() {
		if (instance == null) {
			instance = new OrikaMapper();
		}
		return instance;
	}
}


