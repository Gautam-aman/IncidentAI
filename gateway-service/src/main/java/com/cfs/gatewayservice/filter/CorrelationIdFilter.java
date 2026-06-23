package com.cfs.gatewayservice.filter;


import java.util.UUID;

import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class CorrelationIdFilter
		implements GlobalFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String correlationId =
				UUID.randomUUID().toString();

		ServerWebExchange mutatedExchange = exchange.mutate()
				.request(request -> request.header("X-Correlation-ID", correlationId))
				.build();

		return chain.filter(mutatedExchange);
	}
}
