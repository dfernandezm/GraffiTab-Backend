package com.graffitab.server.api.authentication;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.graffitab.server.api.errors.LoginUserNotActiveException;
import com.graffitab.server.api.errors.MaximumLoginAttemptsException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.persistence.model.user.User.AccountStatus;
import com.graffitab.server.service.AuthenticationService;
import com.graffitab.server.service.user.UserService;

public class JsonLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Resource
	private UserService userService;

	private JSONObject json;

	public JsonLoginAuthenticationFilter() {
		super();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {

		String username = obtainUsername(request);

		if (username == null) {
			return super.attemptAuthentication(request, response);
		} else {
			try {
				return attemptAuthenticationIfActiveUser(username, request, response);
			} catch (AuthenticationException ae) {
				if (ae instanceof BadCredentialsException) {
					try {
						userService.updateLoginAttempts(username);
					} catch(RestApiException rae) {
						String msg = "Maximum login attempts for user [" + username + "]";
						throw new MaximumLoginAttemptsException(msg, rae);
					}
				}

				throw ae;
			}
		}
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		JSONObject jsonResponse = AuthenticationService.getJsonPayload(request);
		if (jsonResponse != null)
			json = jsonResponse;
		return json.getString("username");
	}

	@Override
	protected String obtainPassword(HttpServletRequest request) {
		return json.getString("password");
	}

	protected Authentication attemptAuthenticationIfActiveUser(String usernameOrEmail, HttpServletRequest request, HttpServletResponse response) {

		User user = userService.findByUsernameOrEmail(usernameOrEmail);

		if (user == null) {
			throw new UsernameNotFoundException("The username or email [" + usernameOrEmail + "] cannot be found");
		} else if (user.getAccountStatus() == AccountStatus.ACTIVE) {
			return super.attemptAuthentication(request, response);
		} else {
			String msg = "Current user is not in the expected state [" +
						AccountStatus.ACTIVE.name() + "], it is " +
						user.getAccountStatus().name();
			RestApiException failureCause = new RestApiException(ResultCode.USER_NOT_IN_EXPECTED_STATE, msg);
			throw new LoginUserNotActiveException(msg, failureCause);
		}
	}
}
