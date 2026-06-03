package com.cfs.ticketservice.dto;

import lombok.Data;

@Data
public class AddCommentRequest {
	private String authorId;
	private String comment;
}
