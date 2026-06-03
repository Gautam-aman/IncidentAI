package com.cfs.ticketservice.event;


import java.util.UUID;

import com.cfs.ticketservice.entity.Priority;
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
public class IncidentCreatedEvent {

	private UUID incidentId;
	private String title;
	private Priority priority;

}
