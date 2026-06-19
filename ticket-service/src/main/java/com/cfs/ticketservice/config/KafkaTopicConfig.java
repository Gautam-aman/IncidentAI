package com.cfs.ticketservice.config;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

	@Bean
	public NewTopic incidentCreateTopic() {
		return TopicBuilder
				.name("incident-created")
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic notificationDlq() {

		return TopicBuilder
				.name("notification-dlq")
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic incidentAssignedTopic() {
		return TopicBuilder
				.name("incident-assigned")
				.partitions(3)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic incidentStatusTopic() {
		return TopicBuilder
				.name("incident-status-changed")
				.partitions(3)
				.replicas(1)
				.build();
	}

}
