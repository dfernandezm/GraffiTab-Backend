package com.graffitab.server.persistence.redis;

import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * Created by davidfernandez on 05/07/2016.
 */

@Log4j2
@Service
public class RedisUserSessionService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private FindByIndexNameSessionRepository<? extends ExpiringSession> sessions;

    public void deleteSessionInRedis(String sessionId) {
        redisOperationsSessionRepository.delete(sessionId);
    }

    public void logoutEverywhere(User user, boolean keepCurrentSession) {
        HttpSession session = httpServletRequest.getSession(false);

        if (keepCurrentSession && session == null) {
            String msg = "Current session is null -- this is not possible, investigate!";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        String currentSessionId = session.getId();
        Collection<? extends ExpiringSession> currentUserSessions = getSessionsForCurrentUser();

        currentUserSessions.forEach((userSession) -> {
            if (!keepCurrentSession || !userSession.getId().equals(currentSessionId)) {
                // Delete this session if we aren't keeping the current one (password reset)
                // or we keep the current one (change password), but this is not it
                deleteSessionInRedis(userSession.getId());
            }

            if (log.isDebugEnabled()) {
                log.debug("Deleting sessions for user ID {}", user.getId());
            }
        });
    }

    public Collection<? extends ExpiringSession> getSessionsForCurrentUser() {
        User user = userService.getCurrentUser();
        Collection<? extends ExpiringSession> usersSessions = sessions
                .findByIndexNameAndIndexValue(
                        FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME,
                        user.getUsername())
                .values();

        usersSessions.forEach((session) -> {
            log.info("Found session with id " + session.getId() + " for user " + user.getUsername());
        });

        return usersSessions;
    }

}
