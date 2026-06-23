package com.cfs.notificationservice.service;


import lombok.extern.slf4j.Slf4j;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {


	@Retryable(
			retryFor = Exception.class,
			maxAttempts = 3
	)
	public void sendEmail(
			String recipient,
			String subject,
			String body
	) {

		log.info(
				"""
				Sending Email

				To: {}
				Subject: {}
				Body: {}
				""",
				recipient,
				subject,
				body
		);
	}

}
