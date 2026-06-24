package com.cfs.ticketservice.dto;

import com.cfs.ticketservice.entity.IncidentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
	@NotNull
	private IncidentStatus status;

	@NotBlank
	private String performedBy;
}
