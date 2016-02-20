package com.graffitab.server.service.email;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

@Service

public class SendgridEmailSenderService implements EmailSenderService {

	private static Logger log = LogManager.getLogger();

	private SendGrid sendGrid;

	private String SENDGRID_API_KEY_ENVVAR_NAME = "SENDGRID_APIKEY";

	@PostConstruct
	public void setupSendgrid() {
		String apiKey = System.getenv(SENDGRID_API_KEY_ENVVAR_NAME);
		log.debug("Setting up Sendgrid with API key: {}", apiKey);
		sendGrid = new SendGrid(apiKey);
	}

	@Override
	public void sendEmail(Email email) {

		if (log.isDebugEnabled()) {
			log.debug("[SENDGRID] About to send email " + email.getEmailType() + " to " +
						email.getRecipients()[0] + " through Sendgrid");
		}

		try {

			SendGrid.Email sendGridEmail = new SendGrid.Email();
			sendGridEmail.addTo(email.getRecipients());
			sendGridEmail.setFrom(email.getFromAddress());
			sendGridEmail.setFromName(email.getFromName());
			sendGridEmail.setSubject(email.getSubject());
			sendGridEmail.setHtml(email.getHtmlBody());

			SendGrid.Response response = sendGrid.send(sendGridEmail);

			if (log.isDebugEnabled()) {
				log.debug("[SENDGRID] Email for " + email.getEmailType() + " to " +
							email.getRecipients()[0] + ", respoonse from SendGrid status: " + response.getStatus());
			}
		} catch (SendGridException e) {
			String msg = "Error sending email through SendGrid";
			log.error(msg, e);
			throw new EmailSenderException(msg, e);
		}
	}
}
