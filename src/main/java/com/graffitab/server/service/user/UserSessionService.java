package com.graffitab.server.service.user;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import lombok.extern.log4j.Log4j2;

import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
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

	// 1 day -- time between runs of the session cleaner
	private static final long DAILY = 24 * 60 * 60 * 1000L;

	// 1 week -- any session not touched in the last week will be deleted
	private static final Long SESSION_VALIDITY_INTERVAL = 7 * 24 * 60 * 60 * 1000L;

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
		if (log.isDebugEnabled()) {
			log.debug("Saving session with ID: " + session.getId());
		}

		Map<String, Object> sessionAttributeMap = getSessionAttributeMap(session);
		byte[] sessionData = SerializationUtils.serialize(sessionAttributeMap);

		if (sessionAttributeMap.isEmpty()) {
			if (log.isDebugEnabled()) {
				if (sessionAttributeMap.isEmpty()) {
					log.debug("Session attribute data is empty");
				}
			}
		}

		if (log.isDebugEnabled()) {
			if (sessionData == null) {
				log.debug("sessionData is null");
			}
		}

		transactionUtils.executeInTransaction(() -> {
			if (log.isDebugEnabled()) {
				log.debug("Starting transaction to save session with ID: " + session.getId());
			}

			UserSession currentUserSession = findBySessionIdAndInitialize(session.getId());

			if (currentUserSession == null) {
				User currentUser = userService.getCurrentUser();
				userService.merge(currentUser);
				UserSession userSession = new UserSession();
				userSession.setCreatedOn(new DateTime());
				userSession.setSessionId(session.getId());
				userSession.setContent(sessionData);
				userSession.setUser(currentUser);

				if (log.isDebugEnabled()) {
					log.debug("Persisting session with ID {}", session.getId());
				}

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
		sessionOperationsExecutor.submit(() -> {
			transactionUtils.executeInTransaction(() -> {
				Integer deletedSessionsCount = userSessionDao.createNamedQuery("UserSession.deleteSession")
						 .setParameter("sessionId", sessionId)
						 .executeUpdate();
				if (log.isDebugEnabled()) {
					log.debug("Deleted {} destroyed session [{}]", deletedSessionsCount, sessionId);
				}
			});
		});
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

		if (keepCurrentSession && session == null) {
			String msg = "Current session is null -- this is not possible, investigate!";
			log.error(msg);
			throw new IllegalStateException(msg);
		}

		String queryString = "delete from UserSession us " +
							 " where us.user = :user" +
							 (keepCurrentSession ? " and us.sessionId != :keepSessionId": "");

		Query deleteSessionsQuery = userSessionDao.createQuery(queryString)
				.setParameter("user", user);
		if (keepCurrentSession) {
			deleteSessionsQuery.setParameter("keepSessionId", session.getId());
		}

		Integer deletedSessionsCount = deleteSessionsQuery.executeUpdate();

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

	@Transactional
	public SecurityContext restoreSecurityContextAndHttpSessionFromDb() {
		String requestedSessionId = httpServletRequest.getRequestedSessionId();

		UserSession persistedUserSession = findBySessionId(requestedSessionId);

		//TODO: in UserSession, distinguish between ExternalProviderToken and UsernamePasswordAuthToken
		if (persistedUserSession != null) {
			User user = persistedUserSession.getUser();
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

			SecurityContext context = new SecurityContextImpl();
            context.setAuthentication(authentication);

            // Set this user as the one running this request -- for getCurrentUser() call
            user = userService.findUserById(user.getId());
            ProxyUtilities.initializeObjectWithOneLevelCollections(user);
            RunAsUser.set(user);

            restoreHttpSession(persistedUserSession);

            //TODO: mark requestedSessionId (old one) as to be deleted ?? (only if the clients use the new one)

            return context;
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private void restoreHttpSession(UserSession persistedUserSession) {
		HttpSession httpSession = httpServletRequest.getSession();

        byte[] sessionAttributeData = persistedUserSession.getContent();
        Map<String, Object> sessionAttributeMap =
        		(Map<String, Object>) SerializationUtils.deserialize(sessionAttributeData);

        if (sessionAttributeMap != null) {
            Set<String> keySet = sessionAttributeMap.keySet();
            for (String key : keySet) {
            	httpSession.setAttribute(key, sessionAttributeMap.get(key));
            }
        }

        if (findBySessionId(httpSession.getId()) == null) {
        	// Store new valid session in DB
        	saveOrUpdateSessionData(httpSession);
        }

        // Set timeout for new session: 1 week
        httpSession.setMaxInactiveInterval(604800);
	}

	@Transactional
	public boolean existsSession(String sessionId) {
		return findBySessionId(sessionId) != null;
	}

	public void touchSession(String sessionId) {

		sessionOperationsExecutor.submit(() -> {

			transactionUtils.executeInTransaction(() -> {

				UserSession userSession = findBySessionId(sessionId);

				if (userSession != null) {
					if (log.isDebugEnabled()) {
						log.debug("Touching session {}", sessionId);
					}
					userSession.setCreatedOn(new DateTime());
				} else {
					String msg = "Session to touch with ID {} does not exist in DB -- this cannot happen";
					log.warn(msg);
					throw new IllegalStateException(msg);
				}
			});

		});
	}

	@SuppressWarnings("unchecked")
	@Scheduled(fixedRate = DAILY)
	public void cleanExpiredSessions() {

		if (log.isDebugEnabled()) {
			log.debug("Running cleaner for expired sessions");
		}

		List<String> sessionIds = transactionUtils.executeInTransactionWithResult(() -> {

			DateTime expiryDateTime = new DateTime();
			expiryDateTime = expiryDateTime.minusMillis(SESSION_VALIDITY_INTERVAL.intValue());
			Query query = userSessionDao.createNamedQuery("UserSession.findExpiredSessionIds")
										.setParameter("expiryInterval", expiryDateTime);
			return (List<String>) query.list();
		});

		sessionIds.forEach((sessionId) -> {

			transactionUtils.executeInNewTransaction(() -> {
				Query query = userSessionDao.createNamedQuery("UserSession.deleteSession")
						.setParameter("sessionId", sessionId);

				int deletedSessions = query.executeUpdate();

				if (log.isDebugEnabled()) {
					log.debug("Deleted {} expired session [{}]", deletedSessions, sessionId);
				}
			});
		});
	}
}
