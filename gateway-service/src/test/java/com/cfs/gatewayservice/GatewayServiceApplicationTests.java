package com.cfs.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class GatewayServiceApplicationTests {

	@Autowired
	private RouteLocator routeLocator;

	@Test
	void contextLoads() {
	}

	@Test
	void gatewayRoutesAreLoaded() {
		assertEquals(6L, routeLocator.getRoutes().count().block());
	}

}
