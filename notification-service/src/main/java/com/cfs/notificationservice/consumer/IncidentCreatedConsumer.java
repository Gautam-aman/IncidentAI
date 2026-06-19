package com.cfs.notificationservice.consumer;


import com.cfs.notificationservice.dto.IncidentCreatedEvent;
import com.cfs.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentCreatedConsumer {
	private final EmailService emailService;


	@KafkaListener(
			topics = "incident-created",
			groupId = "notification-group"
	)
	public void consume(
			IncidentCreatedEvent event
	) {

		log.info(
				"Incident Created Event Received {}",
				event.getIncidentId()
		);

		emailService.sendEmail(
				"admin@incidentai.com",
				"New Incident Created",
				event.getTitle()
		);
	}

}
