package com.cfs.authservice.service;


import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
	private final StringRedisTemplate stringRedisTemplate;

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
				stringRedisTemplate.hasKey(token)
		);
	}

}
