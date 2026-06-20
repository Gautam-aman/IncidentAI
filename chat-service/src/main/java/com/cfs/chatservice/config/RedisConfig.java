package com.cfs.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RedisConfig {

	@Bean
	public ChannelTopic chatTopic() {

		return new ChannelTopic(
				"incident-chat"
		);
	}
}
