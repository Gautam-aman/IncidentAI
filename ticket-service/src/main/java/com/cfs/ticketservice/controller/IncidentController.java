package com.cfs.ticketservice.controller;

import java.util.List;
import java.util.UUID;

import com.cfs.ticketservice.dto.AddCommentRequest;
import com.cfs.ticketservice.dto.AssignIncidentRequest;
import com.cfs.ticketservice.dto.CreateIncidentRequest;
import com.cfs.ticketservice.dto.UpdateStatusRequest;
import com.cfs.ticketservice.entity.Incident;
import com.cfs.ticketservice.entity.IncidentAudit;
import com.cfs.ticketservice.entity.IncidentComment;
import com.cfs.ticketservice.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

	private final IncidentService incidentService;

	@PostMapping
	public Incident create(
			@Valid @RequestBody CreateIncidentRequest request
	) {
		return incidentService.createIncident(request);
	}

	@GetMapping("/{id}")
	public Incident get(
			@PathVariable UUID id
	) {
		return incidentService.getIncident(id);
	}

	@PutMapping("/{id}/assign")
	public Incident assign(
			@PathVariable UUID id,
			@RequestBody AssignIncidentRequest request
	) {
		return incidentService.assign(id, request);
	}

	@PutMapping("/{id}/status")
	public Incident updateStatus(
			@PathVariable UUID id,
			@RequestBody UpdateStatusRequest request
	) {
		return incidentService.updateStatus(id, request);
	}

	@PostMapping("/{id}/comments")
	public IncidentComment addComment(
			@PathVariable UUID id,
			@RequestBody AddCommentRequest request
	) {
		return incidentService.addComment(id, request);
	}

	@GetMapping("/{id}/audit")
	public List<IncidentAudit> audit(
			@PathVariable UUID id
	) {
		return incidentService.getAudits(id);
	}

}
