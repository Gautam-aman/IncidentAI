package com.cfs.searchservice;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SearchServiceApplicationTests {

	@Test
	void applicationClassLoads() {
		assertNotNull(SearchServiceApplication.class);
	}

}
