package com.graffitab.server.api.authentication;

import javax.annotation.Resource;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import com.graffitab.server.service.user.UserSessionService;

@Component
@Log4j2
public class UserSessionListener implements HttpSessionListener {

	@Resource
	private UserSessionService userSessionService;

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		if (log.isDebugEnabled()) {
			log.debug("Session created: {}", se.getSession().getId());
		}
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		if (log.isDebugEnabled()) {
			log.debug("Session destroyed: {}", se.getSession().getId());
		}

		userSessionService.deleteSession(se.getSession().getId());
	}

}
