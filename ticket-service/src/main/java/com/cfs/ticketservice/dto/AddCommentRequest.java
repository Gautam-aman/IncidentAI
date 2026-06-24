package com.cfs.ticketservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCommentRequest {
	@NotBlank
	private String authorId;

	@NotBlank
	private String comment;
}
