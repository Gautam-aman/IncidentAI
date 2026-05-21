package com.cfs.authservice.repo;

import java.util.Optional;
import java.util.UUID;

import com.cfs.authservice.entity.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(String name);
}
