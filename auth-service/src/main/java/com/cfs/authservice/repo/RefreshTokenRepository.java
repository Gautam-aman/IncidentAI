package com.cfs.authservice.repo;

import java.util.Optional;
import java.util.UUID;

import com.cfs.authservice.entity.RefreshToken;
import com.cfs.authservice.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	Optional<RefreshToken> findByRefreshToken(String refreshToken);
	Optional<RefreshToken> findByUser(String token);
	void deleteByUser(User User);
}
