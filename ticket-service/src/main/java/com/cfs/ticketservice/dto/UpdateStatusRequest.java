package com.cfs.ticketservice.dto;

import com.cfs.ticketservice.entity.IncidentStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
	private IncidentStatus status;
	private String performedBy;
}
