package com.cfs.authservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.cfs.authservice.dto.AuthResponse;
import com.cfs.authservice.dto.LoginRequest;
import com.cfs.authservice.dto.MessageResponse;
import com.cfs.authservice.dto.RefreshTokenRequest;
import com.cfs.authservice.dto.RegisterRequest;
import com.cfs.authservice.entity.RefreshToken;
import com.cfs.authservice.entity.Role;
import com.cfs.authservice.entity.RoleName;
import com.cfs.authservice.entity.User;
import com.cfs.authservice.repo.RefreshTokenRepository;
import com.cfs.authservice.repo.RoleRepository;
import com.cfs.authservice.repo.UserRepository;
import com.cfs.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
	private final TokenBlacklistService tokenBlacklistService;

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

	public AuthResponse login(LoginRequest loginRequest) {

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
		);

		User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new RuntimeException("User not found"));

		Map<String, Object> claims = new HashMap<>();

		claims.put(
				"roles",
				user.getRoles()
						.stream()
						.map(Role::getName)
						.toList()
		);

		claims.put("userId", user.getId());

		String accessToken =
				jwtUtil.generateToken(user.getEmail(), claims);

		refreshTokenRepository.deleteByUser(user);

		String newRefreshToken = UUID.randomUUID().toString();

		RefreshToken newToken =
				RefreshToken.builder()
						.token(newRefreshToken)
						.user(user)
						.expiryDate(LocalDateTime.now().plusDays(7))
						.revoked(false)
						.build();

		refreshTokenRepository.save(newToken);


		return AuthResponse.builder()
				.accessToken(accessToken)
				.refreshToken(newRefreshToken)
				.userId(user.getId())
				.email(user.getEmail())
				.roles(
						user.getRoles()
								.stream()
								.map(Role::getName)
								.collect(Collectors.toSet())
				)
				.build();
	}

	public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
		RefreshToken token =
				refreshTokenRepository.findByRefreshToken(
								refreshTokenRequest.getRefreshToken()
						)
						.orElseThrow(() ->
								new RuntimeException("Invalid refresh token"));

		if (token.getRevoked()){
			throw new RuntimeException("Refresh Token Revoked");
		}

		if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("Refresh token expired");
		}

		User user = token.getUser();

		Map<String, Object> claims = new HashMap<>();

		claims.put(
				"roles",
				user.getRoles()
						.stream()
						.map(Role::getName)
						.toList()
		);

		claims.put("userId", user.getId());

		String accessToken =
				jwtUtil.generateToken(user.getEmail(), claims);

		return AuthResponse.builder()
				.accessToken(accessToken)
				.refreshToken(token.getToken())
				.userId(user.getId())
				.email(user.getEmail())
				.roles(
						user.getRoles()
								.stream()
								.map(Role::getName)
								.collect(Collectors.toSet())
				)
				.build();

	}

	public MessageResponse logout(String refreshToken) {
		RefreshToken token =
				refreshTokenRepository.findByRefreshToken(refreshToken)
						.orElseThrow(() ->
								new RuntimeException("Invalid refresh token"));

		token.setRevoked(true);
		tokenBlacklistService.blacklistToken(
				refreshToken,
				15 * 60 * 1000
		);
		refreshTokenRepository.save(token);
		return new MessageResponse("Logout successfully");

	}

}
