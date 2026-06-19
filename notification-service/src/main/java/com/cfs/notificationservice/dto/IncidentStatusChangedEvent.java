package com.cfs.notificationservice.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentStatusChangedEvent {

	private UUID incidentId;
	private String oldStatus;
	private String newStatus;
}