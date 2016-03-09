package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import com.graffitab.server.service.user.UserSessionService;

/**
 *
 * Filter needed to detect if the incoming session is still valid or should
 * be invalidated an force the request to authenticate again.
 *
 * All valid sessions must be stored in the database (or other storage for active sessions),
 * otherwise they are detected as invalid.
 *
 * @author david
 *
 */
@Log4j2
public class SessionInvalidationFilter extends GenericFilterBean {

	static final String FILTER_APPLIED = "__invalidate_sessions_applied";

	@Resource
	private UserSessionService userSessionService;

	private AuthenticationEntryPoint commonAuthenticationEntryPoint;

	public SessionInvalidationFilter(AuthenticationEntryPoint aep) {
		this.commonAuthenticationEntryPoint = aep;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (request.getAttribute(FILTER_APPLIED) != null) {
			// ensure that filter is only applied once per request
			chain.doFilter(request, response);
			return;
		}

		if (log.isDebugEnabled()) {
			log.debug("Checking sessions to invalidate...");
		}

	    HttpSession session = request.getSession(false);
	    String currentSessionId = session == null ? null : session.getId();
	    // Performance??
	    if (currentSessionId != null && !userSessionService.exists(currentSessionId)) {
	        // Request is coming from an invalid session.
	    	if (log.isDebugEnabled()) {
				log.debug("Invalidating session: {}",currentSessionId);
			}
	    	// Invalidate this session
	        session.invalidate();

	        request.removeAttribute(FILTER_APPLIED);

	        // Redirect to entryPoint -- user is not authenticated
	        commonAuthenticationEntryPoint.commence(request, response,
	        		new AuthenticationCredentialsNotFoundException("Not authenticated"));

	        return;
	    }

	    try {
	    	chain.doFilter(request, response);
	    } finally {
	    	request.removeAttribute(FILTER_APPLIED);
	    }
	}

}
