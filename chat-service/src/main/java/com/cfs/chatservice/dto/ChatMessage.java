package com.cfs.chatservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

	private String incidentId;

	private String senderId;

	private String senderName;

	private String content;

	private MessageType type;
}
