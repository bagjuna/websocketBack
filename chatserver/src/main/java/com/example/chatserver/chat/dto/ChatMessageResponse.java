package com.example.chatserver.chat.dto;


import com.example.chatserver.chat.domain.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
	private Long roomId;
	private String message;
	private String senderEmail;
	private MessageType type;
}
