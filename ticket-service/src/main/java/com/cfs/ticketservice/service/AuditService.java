package com.cfs.ticketservice.service;


import java.util.UUID;

import com.cfs.ticketservice.entity.IncidentAudit;
import com.cfs.ticketservice.repository.IncidentAuditRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditService {
	private final IncidentAuditRepository incidentAuditRepository;

	public void log(
			UUID incidentId,
			UUID userId,
			String actions,
			String details
	){
		IncidentAudit incidentAudit = IncidentAudit.builder()
				.incidentId(incidentId)
				.userId(userId)
				.action(actions)
				.details(details)
				.build();
		incidentAuditRepository.save(incidentAudit);
	}

}
