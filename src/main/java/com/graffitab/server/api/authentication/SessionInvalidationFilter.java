package com.graffitab.server.api.authentication;

import com.graffitab.server.service.user.UserSessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 *
 * Filter needed to detect if the incoming session is still valid or should
 * be invalidated and force the request to authenticate again.
 *
 * All valid sessions must be stored in the database (or other storage for active sessions),
 * otherwise they are detected as invalid.
 *
 * @author david
 *
 */
@Log4j2
public class SessionInvalidationFilter extends OncePerRequestFilter {

	@Resource
	private UserSessionService userSessionService;

	private AuthenticationEntryPoint commonAuthenticationEntryPoint;

	public SessionInvalidationFilter(AuthenticationEntryPoint aep) {
		this.commonAuthenticationEntryPoint = aep;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		String requestedSessionId = request.getRequestedSessionId();

		if (log.isDebugEnabled()) {
			log.debug("Checking sessions to invalidate...");
		}

		HttpSession session = request.getSession(false);
		String currentSessionId = session == null ? null : session.getId();

		if (currentSessionId != null && !userSessionService.exists(currentSessionId)) {

			// Request is coming from an invalid session.
			if (log.isDebugEnabled()) {
				log.debug("Invalidating session: {}",currentSessionId);
			}

			// Invalidate this session
			session.invalidate();

			// Redirect to entryPoint -- user is not authenticated
			commonAuthenticationEntryPoint.commence(request, response,
					new AuthenticationCredentialsNotFoundException("Not authenticated"));

			return;
		}

		try {

			filterChain.doFilter(request, response);
			log.info("Execution of call " + request.getMethod() + " " + request.getRequestURI() + " took " +
					(System.currentTimeMillis() - startTime) + " ms");

		} finally {

			if (currentSessionId != null) {
				userSessionService.touchSession(currentSessionId);
			} else {
				if (log.isDebugEnabled()) {
					log.debug("The actual session for the current request was NULL and the requested session ID was [" + requestedSessionId + "]");
				}
			}
		}
	}
}
