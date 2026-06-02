package com.cfs.ticketservice.entity;


import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.internal.util.actions.LoadClass;

@Entity
@Table(name = "incident_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentComment {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

	private UUID incidentId;

	private UUID authorId;

	@Column(nullable = false)
	private String comment;

	@CreationTimestamp
	private LocalDateTime createdAt;

}
