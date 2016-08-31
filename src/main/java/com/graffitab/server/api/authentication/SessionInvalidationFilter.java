package com.graffitab.server.api.authentication;

import com.graffitab.server.service.user.UserSessionService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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
 * This is also used to measure the request time in milliseconds
 *
 * @author david
 *
 */

@Log4j2
public class SessionInvalidationFilter extends OncePerRequestFilter {

	@Value("${session.backups.enabled:true}")
	private Boolean sessionBackupsEnabled;

	@Value("${health.status.show:true}")
	private Boolean showHealthStatus;

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
		HttpSession session = request.getSession(false);
		String currentSessionId = session == null ? null : session.getId();

		if (sessionBackupsEnabled) {

			if (log.isDebugEnabled()) {
				log.debug("Checking sessions to invalidate...");
			}

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
		}

		try {

			filterChain.doFilter(request, response);
			String requestUri = request.getRequestURI();

			if (!requestUri.endsWith("status") || (showHealthStatus && requestUri.endsWith("status"))) {
				log.info("Execution of call " + request.getMethod() + " " + request.getRequestURI() + " took " +
						(System.currentTimeMillis() - startTime) + " ms - status " + response.getStatus() +
						" - User Agent: " + request.getHeader("User-Agent"));
			}

		} finally {
			if (currentSessionId != null && sessionBackupsEnabled) {
				userSessionService.touchSession(currentSessionId);
			} else {
				if (sessionBackupsEnabled) {
					if (log.isDebugEnabled()) {
						log.debug("The actual session for the current request was NULL and the requested session ID was [" + requestedSessionId + "]");
					}
				}
			}
		}
	}
}
