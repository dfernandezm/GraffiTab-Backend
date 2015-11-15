package com.graffitab.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.User;

@Configuration
public class DaoConfig {

	@Bean
	public HibernateDaoImpl<User, Long> userDao() {
		HibernateDaoImpl<User, Long> userDao = new HibernateDaoImpl<>();
		userDao.setEntityClass(User.class);
		return userDao;
	}
}
