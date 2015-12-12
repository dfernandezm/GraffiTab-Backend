package com.graffitab.server.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FormLoginController {
	
	@RequestMapping(value = {"/", "/login"}, produces="text/html")
	public String loginWeb() {
		return "home";
	}
}
