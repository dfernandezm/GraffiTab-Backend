package com.graffitab.server.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.streamable.Streamable;
import com.graffitab.server.service.user.UserService;

@Service
public class StreamableService {

	@Resource
	private UserService userService;

	@Resource
	private HibernateDaoImpl<Streamable, Long> streamableDao;
}
