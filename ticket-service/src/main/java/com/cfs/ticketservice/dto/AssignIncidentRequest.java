package com.cfs.ticketservice.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AssignIncidentRequest {
	private UUID assigneeId;
	private UUID performedBy;
}
