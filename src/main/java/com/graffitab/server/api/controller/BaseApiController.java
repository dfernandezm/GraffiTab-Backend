package com.graffitab.server.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.graffitab.server.api.dto.StatusDto;

@Controller
public class BaseApiController {

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public StatusDto getStatus() {
		StatusDto statusDto = new StatusDto();
		statusDto.setStatus("OK");
		return statusDto;
	}
}
