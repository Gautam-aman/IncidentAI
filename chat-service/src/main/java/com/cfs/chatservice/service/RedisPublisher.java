package com.cfs.chatservice.service;

import com.cfs.chatservice.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

	private final StringRedisTemplate redisTemplate;

	private final ObjectMapper mapper;

	public void publish(
			ChatMessage message
	) throws Exception {

		redisTemplate.convertAndSend(
				"incident-chat",
				mapper.writeValueAsString(message)
		);
	}
}
