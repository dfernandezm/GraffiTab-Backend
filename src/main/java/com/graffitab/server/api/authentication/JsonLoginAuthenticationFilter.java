package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.graffitab.server.api.errors.LoginUserNotActiveException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.persistence.model.user.User.AccountStatus;
import com.graffitab.server.service.user.UserService;

public class JsonLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	@Resource
	private UserService userService;

	private JSONObject json;

	public JsonLoginAuthenticationFilter() {
		super();
	}

	private void getPayload(HttpServletRequest request) {
		try {

			if (request.getContentType().equals("application/json")) {
				String payload = IOUtils.toString(request.getInputStream());
				if (payload.length() > 0) {
					this.json = new JSONObject(payload);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException {

		String username = obtainUsername(request);

		if (username == null) {
			return super.attemptAuthentication(request, response);
		} else {
			return attemptAuthenticationIfActiveUser(username, request, response);
		}
	}

	@Override
	protected String obtainUsername(HttpServletRequest request) {
		getPayload(request);
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
