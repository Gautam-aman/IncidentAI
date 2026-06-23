package com.cfs.notificationservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationServiceApplicationTests {

	@Test
	void applicationClassLoads() {
		assertNotNull(NotificationServiceApplication.class);
	}

}
