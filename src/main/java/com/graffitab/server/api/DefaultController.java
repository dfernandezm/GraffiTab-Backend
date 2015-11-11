package com.graffitab.server.api;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.graffitab.server.api.dto.StatusDto;

@RestController
public class DefaultController {

	private static Logger LOG = LogManager.getLogger();
	
	@RequestMapping(value = "/**")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public StatusDto getDefault(HttpServletRequest request) {
		StatusDto statusDto = new StatusDto();
		statusDto.setStatus("NOT FOUND");
		LOG.warn("Uri not found " + request.getRequestURI());
		return statusDto;
	}
	
	
}
