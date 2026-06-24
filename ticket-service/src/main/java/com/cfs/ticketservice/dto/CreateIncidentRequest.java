package com.cfs.ticketservice.dto;

import com.cfs.ticketservice.entity.Priority;
import com.cfs.ticketservice.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIncidentRequest {
	@NotBlank
	private String title;

	private String description;

	private Priority priority;

	private Severity severity;

	@NotBlank
	private String reporterId;
}
