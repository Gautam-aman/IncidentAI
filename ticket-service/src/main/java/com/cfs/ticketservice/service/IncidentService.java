package com.cfs.ticketservice.service;

import java.util.List;
import java.util.UUID;

import com.cfs.ticketservice.dto.AddCommentRequest;
import com.cfs.ticketservice.dto.AssignIncidentRequest;
import com.cfs.ticketservice.dto.CreateIncidentRequest;
import com.cfs.ticketservice.dto.UpdateStatusRequest;
import com.cfs.ticketservice.entity.Incident;
import com.cfs.ticketservice.entity.IncidentAudit;
import com.cfs.ticketservice.entity.IncidentComment;
import com.cfs.ticketservice.entity.IncidentStatus;
import com.cfs.ticketservice.repository.IncidentAuditRepository;
import com.cfs.ticketservice.repository.IncidentCommentRepository;
import com.cfs.ticketservice.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncidentService {

	private final IncidentRepository incidentRepository;
	private final IncidentCommentRepository commentRepository;
	private final IncidentAuditRepository auditRepository;
	private final AuditService auditService;

	public Incident createIncident(CreateIncidentRequest request) {
		Incident incident = Incident.builder()
		.title(request.getTitle())
				.description(request.getDescription())
				.priority(request.getPriority())
				.severity(request.getSeverity())
				.status(IncidentStatus.OPEN)
				.reporterId(UUID.fromString(request.getReporterId()))
				.build();
		Incident saved = incidentRepository.save(incident);
		auditService.log(saved.getId() , saved.getReporterId() , "INCIDENT_CREATED",
				"Incident created");

		return saved;
	}

	public Incident getIncident(UUID id) {
		return incidentRepository.findById(id).orElseThrow(()->
				new RuntimeException("Incident Not Found"));
	}

	public Incident assign(UUID incidentId , AssignIncidentRequest request) {
		Incident incident = getIncident(incidentId);
		incident.setAssigneeId(request.getAssigneeId());
		Incident updatedIncident = incidentRepository.save(incident);

		auditService.log(incidentId , request.getPerformedBy() , "ASSIGNED",
				"Assigned to " + request.getAssigneeId());

		return updatedIncident;
	}

	public Incident updateStatus( UUID incidentId,
			UpdateStatusRequest request){
		Incident incident = getIncident(incidentId);
		IncidentStatus old = incident.getStatus();
		incident.setStatus(request.getStatus());
		Incident updatedIncident = incidentRepository.save(incident);

		auditService.log(
				incidentId,
				UUID.fromString(request.getPerformedBy()),
				"STATUS_CHANGED",
				old + " -> " + request.getStatus()
		);

		return updatedIncident;
	}

	public IncidentComment addComment(UUID incidentId,
			AddCommentRequest request){

		IncidentComment comment =
				IncidentComment.builder()
						.incidentId(incidentId)
						.authorId(UUID.fromString(request.getAuthorId()))
						.comment(request.getComment())
						.build();


		IncidentComment savedComment = commentRepository.save(comment);

		auditService.log(
				incidentId,
				UUID.fromString(request.getAuthorId()),
				"COMMENT_ADDED",
				request.getComment()
		);

		return savedComment;
	}

	public List<IncidentAudit> getAudits(UUID incidentId){
		return auditRepository.findByIncidentId(incidentId);
	}
}
