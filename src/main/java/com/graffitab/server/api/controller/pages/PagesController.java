package com.graffitab.server.api.controller.pages;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.graffitab.server.service.user.UserService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class PagesController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/getstarted", method = RequestMethod.GET)
	public String getGetStartedPage() {
		return "index";
	}

	@RequestMapping(value = {"/activate/{token}"})
	public String getActivatePage(@PathVariable(value = "token") String token, Model model,
								  Locale locale,
								  @RequestParam(value = "lang", required = false) String lang) {
		try {
			userService.activateUser(token);
		} catch (Throwable t) {
			log.error("Error activating user with token " + token, t);
			model.addAttribute("lang", lang);
			return "activateError";
		}
		return "activate";
	}

	@RequestMapping(value = {"/resetpassword/{token}"}, method= RequestMethod.GET)
	public String getCompleteResetPasswordPage(@PathVariable(value = "token") String token,
											   @RequestParam(value = "lang", required = false) String lang,
											   Model model) {
		ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
		resetPasswordForm.setToken(token);
		model.addAttribute("resetPasswordForm", resetPasswordForm);
		model.addAttribute("token", token);
		model.addAttribute("lang", lang);
		return "resetPassword";
	}

	@RequestMapping(value = "/resetpassword/{token}", method = RequestMethod.POST)
	public String postCompleteResetPassword(@PathVariable("token") String token,
											@ModelAttribute ResetPasswordForm resetPasswordForm,
											@RequestParam(value = "lang", required = false) String lang,
											Errors errors,
											Model model,
											Locale locale,
											HttpServletRequest request) {

		if (!passwordsAreProvided(resetPasswordForm)) {
			errors.rejectValue("newPassword", "password.blank");
			model.addAttribute("token", token);
			model.addAttribute("lang", lang);
			return "resetPassword";
		}

		if (validatePasswordsMatch(resetPasswordForm)) {
			try {
				userService.completePasswordReset(resetPasswordForm.getToken(), resetPasswordForm.getNewPassword());
			} catch (Throwable t) {
				log.warn("Cannot reset password for token " + token, t);
				model.addAttribute("lang", lang);
				return "resetPasswordError";
			}
			log.info("Password is reset " + resetPasswordForm.getToken() + " / " + resetPasswordForm.getNewPassword());
		} else {
			errors.rejectValue("newPassword", "password.dontMatch");
			model.addAttribute("token", token);
			model.addAttribute("lang", lang);
			return "resetPassword";
		}
		model.addAttribute("lang", lang);
		return "redirect:/resetpasswordsuccess";
	}

	@RequestMapping(value = {"/resetpasswordsuccess"}, method= RequestMethod.GET)
	public String getResetPasswordSuccessPage(@RequestParam(value = "lang", required = false) String lang, Model model, Locale locale) {
		model.addAttribute("lang", lang);
		return "resetPasswordResult";
	}

	private boolean passwordsAreProvided(ResetPasswordForm resetPasswordForm) {
		return StringUtils.hasText(resetPasswordForm.getNewPassword()) &&
				StringUtils.hasText(resetPasswordForm.getRepeatedPassword());
	}

	private boolean validatePasswordsMatch(ResetPasswordForm resetPasswordForm) {
		return resetPasswordForm.getNewPassword().equals(resetPasswordForm.getRepeatedPassword());
	}
}
