package com.cfs.chatservice.listener;

import com.cfs.chatservice.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSubscriber {

	private final ObjectMapper mapper;

	private final SimpMessagingTemplate messagingTemplate;

	public void receiveMessage(
			String payload
	) throws Exception {

		ChatMessage message =
				mapper.readValue(
						payload,
						ChatMessage.class
				);

		messagingTemplate.convertAndSend(
				"/topic/incidents/" +
						message.getIncidentId(),
				message
		);
	}
}
