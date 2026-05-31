package com.cfs.authservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class UserProfileResponse {
	private UUID id;
	private String name;
	private String email;
	private Set<String> roles;
}
