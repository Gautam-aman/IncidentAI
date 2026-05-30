package com.cfs.authservice.service;


import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
	private final StringRedisTemplate stringRedisTemplate;

	private final RedisTemplate redisTemplate;

	public void blacklistToken(String token , long ttlMillis) {
		stringRedisTemplate.opsForValue().set(
				token,
				"BLACKLISTED",
				ttlMillis,
				TimeUnit.MILLISECONDS
		);
	}

	public boolean isBlacklisted(String token) {

		return Boolean.TRUE.equals(
				redisTemplate.hasKey(token)
		);
	}

}
