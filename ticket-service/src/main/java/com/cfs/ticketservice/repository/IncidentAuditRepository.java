package com.cfs.ticketservice.repository;

import java.util.List;
import java.util.UUID;

import com.cfs.ticketservice.entity.IncidentAudit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentAuditRepository extends JpaRepository<IncidentAudit, UUID> {
	List<IncidentAudit> findByIncidentId(UUID incidentId);
}
