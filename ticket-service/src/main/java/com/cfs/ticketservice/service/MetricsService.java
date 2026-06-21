package com.cfs.ticketservice.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

	private final Counter incidentCreatedCounter;

	private final Counter commentCounter;

	public MetricsService(
			MeterRegistry registry
	) {

		this.incidentCreatedCounter =
				Counter.builder(
								"incident_created_total"
						)
						.description(
								"Total incidents created"
						)
						.register(registry);

		this.commentCounter =
				Counter.builder(
								"incident_comment_total"
						)
						.description(
								"Total comments added"
						)
						.register(registry);
	}

	public void incrementIncidentCreated() {
		incidentCreatedCounter.increment();
	}

	public void incrementCommentCreated() {
		commentCounter.increment();
	}
}
