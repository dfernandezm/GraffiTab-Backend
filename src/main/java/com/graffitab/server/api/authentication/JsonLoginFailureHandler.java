package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;

import com.graffitab.server.api.errors.ExternalProviderTokenInvalidException;
import com.graffitab.server.api.errors.LoginUserNotActiveException;
import com.graffitab.server.api.errors.MaximumLoginAttemptsException;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;

// Rest-based Auth
public class JsonLoginFailureHandler implements AuthenticationFailureHandler {

	@Resource(name = "delegateJacksonHttpMessageConverter")
	private MappingJackson2HttpMessageConverter jsonConverter;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		JSONObject json = new JSONObject();
		String resultCode = ResultCode.USER_NOT_LOGGED_IN.name();

		String message;
		if (exception instanceof BadCredentialsException) {
			message = "Invalid username/password provided";
		} else if (exception instanceof LoginUserNotActiveException) {
			LoginUserNotActiveException loginUserNotActiveException = (LoginUserNotActiveException) exception;
			RestApiException restApiException = (RestApiException) loginUserNotActiveException.getCause();
			message = restApiException.getMessage();
			resultCode = restApiException.getResultCode().name();
		} else if (exception instanceof ExternalProviderTokenInvalidException) {
			ExternalProviderTokenInvalidException externalProviderTokenInvalidException = (ExternalProviderTokenInvalidException) exception;
			RestApiException restApiException = (RestApiException) externalProviderTokenInvalidException.getCause();
			message = restApiException.getMessage();
			resultCode = restApiException.getResultCode().name();
		} else if (exception instanceof MaximumLoginAttemptsException) {
			MaximumLoginAttemptsException maximumLoginAttemptsException = (MaximumLoginAttemptsException) exception;
			RestApiException restApiException = (RestApiException) maximumLoginAttemptsException.getCause();
			message = restApiException.getMessage();
			resultCode = restApiException.getResultCode().name();
		} else if (exception instanceof UsernameNotFoundException) {
			message = exception.getMessage();
			resultCode = ResultCode.USER_NOT_FOUND.name();
		} else {
			message = exception.getMessage();
		}

		json.put("resultCode", resultCode);
		json.put("resultMessage", message);

		// Dirty hack to get rid once and for all of the session cookie, as it always added by login endpoints
		// even when the session is invalidated. An 'invalidation cookie' (with 0 age) does not work as
		// expected as the 'Set-Cookie' header is added twice. Overriding the header overcomes this.
		String cookiePath = request.getContextPath();

		if (!StringUtils.hasLength(cookiePath)) {
			cookiePath = "/";
		}

		response.setHeader("Set-Cookie", "JSESSIONID=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=" +
							cookiePath);

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		IOUtils.write(json.toString(), response.getOutputStream());

		// Invalidate the created session as login failed
		HttpSession currentSession = request.getSession(false);
        if (currentSession != null) {
            currentSession.invalidate();
        }
	}
}
