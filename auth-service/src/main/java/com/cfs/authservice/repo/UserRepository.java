package com.cfs.authservice.repo;

import java.util.Optional;
import java.util.UUID;

import com.cfs.authservice.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
	Optional<User> findById(UUID uuid);
	Optional<User> findByEmail(String email);
	Boolean existsByEmail(String email);
}
