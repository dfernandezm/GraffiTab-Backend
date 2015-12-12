package com.graffitab.server.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.graffitab.server.api.dto.StatusDto;

//TODO: handle properly CSS and favicon.ico errors!
@Controller
public class DefaultController {

	private static Logger LOG = LogManager.getLogger();
	
	@RequestMapping(value = "/**")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public @ResponseBody StatusDto getDefault(HttpServletRequest request) {
		StatusDto statusDto = new StatusDto();
		statusDto.setStatus("NOT FOUND");
		LOG.warn("Uri not found " + request.getRequestURI());
		return statusDto;
	}
	
	
}
