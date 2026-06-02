package com.cfs.ticketservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.cfs.ticketservice.entity.IncidentComment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentCommentRepository extends JpaRepository<IncidentComment, UUID> {
	List<IncidentComment> findByIncidentId(UUID incidentId);
}
