package com.graffitab.server.service.user;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import lombok.extern.log4j.Log4j2;

import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.graffitab.server.api.errors.UserNotLoggedInException;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.persistence.model.user.UserSession;
import com.graffitab.server.service.ProxyUtilities;
import com.graffitab.server.service.TransactionUtils;

@Service
@Log4j2
public class UserSessionService {

	@Resource
	private HibernateDaoImpl<UserSession, Long> userSessionDao;

	@Resource
	private UserService userService;

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private HttpServletRequest httpServletRequest;

	private ExecutorService sessionOperationsExecutor = Executors.newFixedThreadPool(2);

	@Transactional
	public UserSession findBySessionIdAndInitialize(String sessionId) {

		UserSession userSession = (UserSession) userSessionDao.getBaseCriteria()
				                  .add(Restrictions.eq("sessionId", sessionId)).uniqueResult();

		if (userSession != null) {
			ProxyUtilities.unwrapProxy(userSession.getUser());
		}
		return userSession;
	}

	@Transactional
	public UserSession findBySessionId(String sessionId) {
		UserSession userSession = (UserSession) userSessionDao.getBaseCriteria()
                .add(Restrictions.eq("sessionId", sessionId)).uniqueResult();
		return userSession;
	}

	@Transactional
	public boolean exists(String sessionId) {
		return findBySessionId(sessionId) != null;
	}

	public void saveOrUpdateSessionData(HttpSession session) {

		log.warn("Saving session with ID: " + session.getId());

		Map<String, Object> sessionAttributeMap = getSessionAttributeMap(session);
		byte[] sessionData = SerializationUtils.serialize(sessionAttributeMap);

		if (sessionAttributeMap.isEmpty()) {
			log.warn("Session attribute data is empty");
		}

		if (sessionData == null) {
			log.warn("sessionData is null");
		}

		transactionUtils.executeInTransaction(() -> {
			log.warn("Starting transaction to save session with ID: " + session.getId());
			UserSession currentUserSession = findBySessionIdAndInitialize(session.getId());
			if (currentUserSession == null) {
				log.warn("Session with ID {} not found, creating new ", session.getId());
				User currentUser = userService.getCurrentUser();
				userService.merge(currentUser);
				UserSession userSession = new UserSession();
				userSession.setCreatedOn(new DateTime());
				userSession.setSessionId(session.getId());
				userSession.setContent(sessionData);
				userSession.setUser(currentUser);

				log.warn("About to persist session with ID {}", session.getId());
				userSessionDao.persist(userSession);
			} else {
				currentUserSession.setContent(sessionData);
			}
		});
	}

	public void saveSessionDataInBackground(HttpSession session) {
	  sessionOperationsExecutor.submit(() -> {
		  saveOrUpdateSessionData(session);
	  });
	}

	@Transactional
	public void deleteSession(String sessionId) {
		Integer deletedSessionsCount = userSessionDao.createNamedQuery("UserSession.deleteSession")
												 .setParameter("sessionId", sessionId)
												 .executeUpdate();
		if (log.isDebugEnabled()) {
			log.debug("Deleted " + deletedSessionsCount + " sessions");
		}
	}

	private Map<String, Object> getSessionAttributeMap(HttpSession session) {
		Map<String, Object> sessionAttributeMap = new HashMap<>();
		if (session != null) {
			Enumeration<String> sessionAttributeNames = session.getAttributeNames();
			while (sessionAttributeNames.hasMoreElements()) {
				String attributeName = sessionAttributeNames.nextElement();
				// Don't copy the spring security context, we'll rebuild it using Session Registry
				if (!attributeName.equals(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)) {
					Object attributeValue = session.getAttribute(attributeName);
					sessionAttributeMap.put(attributeName, attributeValue);
				}
			}
		}
		return sessionAttributeMap;
	}

	public void logoutEverywhere(User user, boolean keepCurrentSession) {

		HttpSession session = httpServletRequest.getSession(false);

		if (session == null) {
			String msg = "Current session is null -- this is not possible, investigate!";
			log.error(msg);
			throw new IllegalStateException(msg);
		}

		String queryString = "delete from UserSession us " +
							 " where us.user = :user" +
							 (keepCurrentSession ? " and us.sessionId != :keepSessionId": "");

		Integer deletedSessionsCount = userSessionDao.createQuery(queryString)
						.setParameter("user", user)
						.setParameter("keepSessionId", session.getId())
						.executeUpdate();


		if (log.isDebugEnabled()) {
			log.debug("Deleted {} sessions for user ID {}", deletedSessionsCount, user.getId());
		}
	}

	public void saveOrUpdateCurrentSessionData() {
		if (httpServletRequest != null) {
			HttpSession session = httpServletRequest.getSession(false);
			if (session != null) {
				if (log.isDebugEnabled()) {
					log.debug("Persisting session: {}", session.getId());
				}
				saveOrUpdateSessionData(session);
			} else {
				log.warn("No session for this request -- this should be investigated");
				throw new UserNotLoggedInException("User is not authenticated or session has not being created");
			}
		} else {
			String msg = "This is not an HTTP request thread";
			log.warn(msg);
			throw new IllegalStateException(msg);
		}
	}

	//TODO: cleaner thread to expire sessions in the DB
}
