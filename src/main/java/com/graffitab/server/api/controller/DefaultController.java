package com.graffitab.server.api.controller;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.controller.pages.ResetPasswordForm;
import com.graffitab.server.api.dto.ActionCompletedResult;
import com.graffitab.server.service.user.UserService;
import com.sun.org.glassfish.external.statistics.annotations.Reset;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.graffitab.server.api.dto.StatusDto;

@Log4j2
@Controller
public class DefaultController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/**")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody StatusDto getDefault(HttpServletRequest request) {
		StatusDto statusDto = new StatusDto();
		statusDto.setStatus("NOT FOUND");
		log.warn("Uri not found " + request.getRequestURI());
		return statusDto;
	}

	@RequestMapping(value = {"/","/home"})
	@ResponseStatus(HttpStatus.OK)
	public String getBasePage(HttpServletRequest request, Model model) {
		return "home";
	}
}
