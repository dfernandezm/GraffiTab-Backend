package com.graffitab.server.api.authentication;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.graffitab.server.api.dto.user.UserDto;
import com.graffitab.server.api.dto.user.result.GetUserResult;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.user.User;
import com.graffitab.server.service.user.UserService;
import com.graffitab.server.service.user.UserSessionService;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonResponseLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Resource(name = "delegateJacksonHttpMessageConverter")
	private MappingJackson2HttpMessageConverter jsonConverter;

	@Resource
	private UserSessionService userSessionService;

	@Resource
	private UserService userService;

	@Resource
	private OrikaMapper mapper;

	private boolean storeSession;

	public JsonResponseLoginSuccessHandler(boolean storeSession) {
		this.storeSession = storeSession;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		GetUserResult result = new GetUserResult();
		UserDto dto = mapper.map((User)authentication.getPrincipal(), UserDto.class);
		result.setUser(dto);

		if (storeSession) {
			if (request.getSession(false) == null) {
				log.warn("The HTTP Session is null at this point, but it should not be");
			}

			HttpSession session = request.getSession(false);
			userSessionService.saveSessionDataInBackground(session);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Session won't be stored for this request -- there must be username / password query params");
			}
		}

		userService.resetLoginAttempts(dto.getUsername());

		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json");
		jsonConverter.getObjectMapper().writeValue(response.getOutputStream(), result);

	}
}
