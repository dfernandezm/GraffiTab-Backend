package com.graffitab.server.config;

import java.io.Serializable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.model.Device;
import com.graffitab.server.persistence.model.User;

@Configuration
public class DaoConfig {

	@Bean
	public HibernateDaoImpl<User, Long> userDao() {
		return generateDao(User.class);
	}

	@Bean
	public HibernateDaoImpl<Asset, Long> assetDao() {
		return generateDao(Asset.class);
	}

	@Bean
	public HibernateDaoImpl<Device, Long> deviceDao() {
		return generateDao(Device.class);
	}

	private <T extends Identifiable<K>,K extends Serializable> HibernateDaoImpl<T,K> generateDao(Class<T> entityClass) {
		HibernateDaoImpl<T, K> dao = new HibernateDaoImpl<>();
		dao.setEntityClass(entityClass);
		return dao;
	}
}
