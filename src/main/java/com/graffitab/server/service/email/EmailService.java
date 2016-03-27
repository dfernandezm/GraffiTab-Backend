package com.graffitab.server.service.email;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class EmailService {

	@Resource
	private EmailSenderService emailSenderService;

	private ExecutorService emailExecutor = Executors.newFixedThreadPool(2);

	public void sendWelcomeEmail(String username, String email, String activationLink) {
		Map<String,String> data = new HashMap<>();
		data.put("@username", username);
		data.put("@activation_link", activationLink);

		Email welcomeEmail = Email.welcome(new String[] {email}, data);
		sendEmailAsync(welcomeEmail);
	}

	public void sendWelcomeExternalEmail(String username, String email) {
		Map<String,String> data = new HashMap<>();
		data.put("@username", username);

		Email welcomeEmail = Email.welcomeExternal(new String[] {email}, data);
		sendEmailAsync(welcomeEmail);
	}

	public void sendResetPasswordEmail(String email, String resetPasswordLink) {
		Map<String,String> data = new HashMap<>();
		data.put("@reset_link", resetPasswordLink);

		Email resetPasswordEmail = Email.resetPassword(new String[] {email}, data);
		sendEmailAsync(resetPasswordEmail);
	}

	public void sendFeedbackEmail(String name, String email, String text) {
		Map<String,String> data = new HashMap<>();
		data.put("@username", name);
		data.put("@email", email);
		data.put("@feedback", text);

		Email feedbackEmail = Email.feedback(data);
		sendEmailAsync(feedbackEmail);
	}

	public void sendFlagEmail(Long streamableId, String streamableLink) {
		Map<String,String> data = new HashMap<>();
		data.put("@streamable_id", streamableId + "");
		data.put("@streamable_link", streamableLink);

		Email feedbackEmail = Email.flag(data);
		sendEmailAsync(feedbackEmail);
	}

	private void sendEmailAsync(Email email) {
		emailExecutor.submit(() -> {
			log.debug("About to send email " + email);
			try {
				emailSenderService.sendEmail(email);
			} catch (Throwable t) {
				log.error("Error sending email", t);
			}
		});
	}
}
