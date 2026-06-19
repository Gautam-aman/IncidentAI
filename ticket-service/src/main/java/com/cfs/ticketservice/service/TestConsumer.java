package com.cfs.ticketservice.service;


import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestConsumer {

	@KafkaListener(
			topics = "incident-created",
			groupId = "ticket-test"
	)
	public void consume(Object event) {

		log.info(
				"Received Event {}",
				event
		);
	}

}
