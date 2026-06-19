package com.cfs.notificationservice.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentAssignedEvent {

	private UUID incidentId;
	private UUID assigneeId;
}
