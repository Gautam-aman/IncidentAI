package com.cfs.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class LoggingFilter
		implements GlobalFilter {

	@Override
	public reactor.core.publisher.Mono<Void> filter(
			ServerWebExchange exchange,
			GatewayFilterChain chain
	) {

		log.info(
				"Incoming Request: {} {}",
				exchange.getRequest().getMethod(),
				exchange.getRequest().getURI()
		);

		return chain.filter(exchange);
	}
}
