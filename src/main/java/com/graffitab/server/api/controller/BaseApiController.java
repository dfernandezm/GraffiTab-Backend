package com.graffitab.server.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.graffitab.server.api.dto.StatusDto;

@RestController
public class BaseApiController {

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public StatusDto getStatus() {
		StatusDto statusDto = new StatusDto();
		statusDto.setStatus("OK");
		return statusDto;
	}
}
