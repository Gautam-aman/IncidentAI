package com.cfs.ticketservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TicketServiceApplicationTests {

	@Test
	void applicationClassLoads() {
		assertNotNull(TicketServiceApplication.class);
	}

}
