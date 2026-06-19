package com.cfs.ticketservice.service;

import lombok.RequiredArgsConstructor;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publish(String topic , Object Event){
		kafkaTemplate.send(topic, Event);
	}

	public void publishToDlq(
			Object event
	) {
		kafkaTemplate.send(
				"notification-dlq",
				event
		);
	}

}
