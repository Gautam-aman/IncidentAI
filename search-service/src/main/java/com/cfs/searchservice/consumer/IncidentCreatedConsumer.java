package com.cfs.searchservice.consumer;

import com.cfs.searchservice.document.IncidentDocument;
import com.cfs.searchservice.dto.IncidentCreatedEvent;
import com.cfs.searchservice.service.SearchService;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncidentCreatedConsumer {

	private final SearchService searchService;

	@KafkaListener(
			topics = "incident-created",
			groupId = "search-group"
	)
	public void consume(
			IncidentCreatedEvent event
	) {

		IncidentDocument document =
				IncidentDocument.builder()
						.id(event.getIncidentId().toString())
						.title(event.getTitle())
						.description(event.getDescription())
						.priority(event.getPriority())
						.severity(event.getSeverity())
						.status(event.getStatus())
						.build();

		searchService.index(document);
	}
}
