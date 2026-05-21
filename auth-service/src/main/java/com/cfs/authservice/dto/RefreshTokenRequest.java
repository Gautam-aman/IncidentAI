package com.cfs.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

	@NotBlank(message = "Token is Required")
	private String refreshToken;

}
