package com.cfs.gatewayservice;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GatewayServiceApplicationTests {

	@Autowired
	private RouteLocator routeLocator;

	@Test
	void contextLoads() {
	}

	@Test
	void gatewayRoutesAreLoaded() {
		Set<String> routeIds = routeLocator.getRoutes()
				.map(route -> route.getId())
				.collectList()
				.block()
				.stream()
				.collect(Collectors.toSet());

		assertTrue(routeIds.containsAll(Set.of(
				"auth-service",
				"ticket-service",
				"search-service",
				"notification-service",
				"chat-service",
				"rag-service",
				"auth-health",
				"ticket-health",
				"search-health",
				"notification-health",
				"chat-health",
				"rag-health"
		)));
	}

}
