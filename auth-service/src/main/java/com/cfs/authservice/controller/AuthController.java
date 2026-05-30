package com.cfs.authservice.controller;

import com.cfs.authservice.dto.AuthResponse;
import com.cfs.authservice.dto.LoginRequest;
import com.cfs.authservice.dto.LogoutRequest;
import com.cfs.authservice.dto.MessageResponse;
import com.cfs.authservice.dto.RefreshTokenRequest;
import com.cfs.authservice.dto.RegisterRequest;
import com.cfs.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/register")
	public MessageResponse registerUser(@Valid @RequestBody RegisterRequest registerRequest){
		return authService.register(registerRequest);
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest){
		return authService.login(loginRequest);
	}

	@PostMapping("/refresh")
	public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request){
		return authService.refreshToken(request);
	}

	@GetMapping("/me")
	public String currentUser(Authentication authentication){
		return authentication.getName();
	}

	@PostMapping("/logout")
	public MessageResponse logout(
			@Valid @RequestBody LogoutRequest request
	) {
		return authService.logout(
				request.getRefreshToken(),request.getRefreshToken()
		);
	}

}
