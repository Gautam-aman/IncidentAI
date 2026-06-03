package com.cfs.ticketservice.repository;

import java.util.UUID;

import com.cfs.ticketservice.entity.Incident;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

}
