package com.cfs.ticketservice.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "incidents")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Incident {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

	@Column(nullable = false)
	private String title;

	@Column(length = 5000)
	private String description;

	@Enumerated(EnumType.STRING)
	private IncidentStatus status;

	@Enumerated(EnumType.STRING)
	private Priority priority;

	@Enumerated(EnumType.STRING)
	private Severity severity;

	private UUID reporterId;

	private UUID assigneeId;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

}
