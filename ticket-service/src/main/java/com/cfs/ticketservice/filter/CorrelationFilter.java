package com.cfs.ticketservice.filter;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jboss.logging.MDC;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationFilter
		extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain
	) throws ServletException, IOException {

		String correlationId =
				request.getHeader(
						"X-Correlation-ID"
				);

		if(correlationId == null) {
			correlationId =
					UUID.randomUUID().toString();
		}

		MDC.put(
				"correlationId",
				correlationId
		);

		try {
			chain.doFilter(
					request,
					response
			);
		}
		finally {
			MDC.clear();
		}
	}
}
