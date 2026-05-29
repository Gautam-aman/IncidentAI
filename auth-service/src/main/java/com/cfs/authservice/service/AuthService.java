package com.cfs.authservice.service;

import com.cfs.authservice.dto.MessageResponse;
import com.cfs.authservice.dto.RegisterRequest;
import com.cfs.authservice.entity.Role;
import com.cfs.authservice.entity.RoleName;
import com.cfs.authservice.entity.User;
import com.cfs.authservice.repo.RefreshTokenRepository;
import com.cfs.authservice.repo.RoleRepository;
import com.cfs.authservice.repo.UserRepository;
import com.cfs.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public MessageResponse register(RegisterRequest registerRequest) {
		if (userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new RuntimeException("Email already registered");
		}

		Role userRole = roleRepository.findByName(
				RoleName.ROLE_USER.name()
		)
				.orElseThrow(()-> new RuntimeException("Role not found"));

		User user = User.builder()
				.username(registerRequest.getName())
				.email(registerRequest.getEmail())
				.password(passwordEncoder.encode(registerRequest.getPassword()))
				.enabled(true)
				.build();

		user.getRoles().add(userRole);
		userRepository.save(user);
		return new MessageResponse("User registered successfully");
	}



}
