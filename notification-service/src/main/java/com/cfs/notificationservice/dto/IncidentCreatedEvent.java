package com.cfs.notificationservice.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentCreatedEvent {

	private UUID incidentId;
	private String title;
	private String priority;
}
