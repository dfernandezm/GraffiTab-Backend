package com.graffitab.server.service.email;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger log = LogManager.getLogger();

	@Resource
	private EmailSenderService emailSenderService;

	private ExecutorService emailExecutor = Executors.newFixedThreadPool(2);

	public void prepareAndSendWelcomeEmail(String username, String email, String activationLink) {
		Map<String,String> data = new HashMap<>();
		data.put("@username", username);
		data.put("@activation_link", activationLink);

		Email welcomeEmail = Email.welcome(new String[] {email}, data);

		emailExecutor.submit(() -> {
			log.debug("About to send email " + email);
			try {
				emailSenderService.sendEmail(welcomeEmail);
			} catch (Throwable t) {
				log.error("Error sending email", t);
			}
		});
	}

}
