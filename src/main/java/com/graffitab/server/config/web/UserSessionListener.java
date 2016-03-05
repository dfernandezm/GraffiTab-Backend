package com.graffitab.server.config.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import com.graffitab.server.service.UserSessionService;

@Component
@Log4j2
public class UserSessionListener implements HttpSessionListener {

	@Resource
	private UserSessionService userSessionService;

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		log.info("Session created: {}", se.getSession().getId());
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		log.info("Session destroyed: {}", se.getSession().getId());
		//TODO: performance?
		userSessionService.deleteSession(se.getSession().getId());
	}

}
