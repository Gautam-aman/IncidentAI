package com.cfs.notificationservice.consumer;

import com.cfs.notificationservice.dto.IncidentAssignedEvent;
import com.cfs.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncidentAssignedConsumer {

	private final EmailService emailService;

	@KafkaListener(
			topics = "incident-assigned",
			groupId = "notification-group"
	)
	public void consume(
			IncidentAssignedEvent event
	) {

		emailService.sendEmail(
				"engineer@incidentai.com",
				"Incident Assigned",
				event.getIncidentId().toString()
		);
	}
}
