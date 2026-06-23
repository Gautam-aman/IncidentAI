package com.cfs.authservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthServiceApplicationTests {

	@Test
	void applicationClassLoads() {
		assertNotNull(AuthServiceApplication.class);
	}

}
