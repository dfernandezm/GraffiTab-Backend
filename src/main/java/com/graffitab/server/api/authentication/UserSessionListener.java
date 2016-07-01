package com.graffitab.server.api.authentication;

import com.graffitab.server.service.user.UserSessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
@Log4j2
public class UserSessionListener implements HttpSessionListener {

	@Value("${session.backups.enabled:true}")
	private Boolean sessionBackupsEnabled;

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

		if (sessionBackupsEnabled) {
			userSessionService.deleteSession(se.getSession().getId());
		}
	}

}
