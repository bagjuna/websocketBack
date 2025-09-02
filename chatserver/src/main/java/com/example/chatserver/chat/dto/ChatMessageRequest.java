package com.example.chatserver.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequest {
	private Long roomId;
	private String message;
	private String senderEmail;
}
