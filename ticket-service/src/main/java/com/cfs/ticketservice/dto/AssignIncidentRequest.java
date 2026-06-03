package com.cfs.ticketservice.dto;

import lombok.Data;

@Data
public class AssignIncidentRequest {
	private String assigneeId;
	private String performedBy;
}
