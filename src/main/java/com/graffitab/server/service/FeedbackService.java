package com.graffitab.server.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.graffitab.server.service.email.EmailService;

@Service
public class FeedbackService {

	@Resource
	private EmailService emailService;

	public void sendFeedback(String name, String email, String text) {
		emailService.sendFeedbackEmail(name, email, text);
	}
}
