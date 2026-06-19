package com.cfs.ticketservice.event;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
