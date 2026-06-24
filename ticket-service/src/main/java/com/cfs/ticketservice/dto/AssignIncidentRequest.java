package com.cfs.ticketservice.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignIncidentRequest {
	@NotNull
	private UUID assigneeId;

	@NotNull
	private UUID performedBy;
}
