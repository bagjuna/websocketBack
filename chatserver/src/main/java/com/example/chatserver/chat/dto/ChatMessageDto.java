package com.example.chatserver.chat.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
	private Long roomId;
	private String message;
	private String senderEmail;
	private LocalDateTime createdTime;
}
