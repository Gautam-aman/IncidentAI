package com.cfs.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

	@NotNull(message = "Name can't be null")
	private String name;

	@Email(message = "Enter Valid Email")
	@NotBlank(message = "Email Can't be Null")
	private String email;

	@NotBlank(message = "Enter password")
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;


}
