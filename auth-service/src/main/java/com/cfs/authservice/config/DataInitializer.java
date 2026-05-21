package com.cfs.authservice.config;


import com.cfs.authservice.entity.Role;
import com.cfs.authservice.entity.RoleName;
import com.cfs.authservice.repo.RoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
	// CommandLineRunner --> automatic run when app start
	private final RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		for(RoleName roleName : RoleName.values()) {
			roleRepository.findByName(roleName.name())
					.orElseGet(()->
							roleRepository.save(Role.builder().name(roleName.name()).build()));
		}
	}
}
