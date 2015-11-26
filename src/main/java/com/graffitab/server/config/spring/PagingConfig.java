package com.graffitab.server.config.spring;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.service.PagingService;

@Configuration
public class PagingConfig {

	@Resource
	private HibernateDaoImpl<User, Long> userDao;
	
	@Bean
	public PagingService<User> pagingServiceUser() {
		PagingService<User> pagingService = new PagingService<>(userDao);
		return pagingService;
	}
}
