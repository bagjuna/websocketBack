package com.example.chatserver.chat.controller;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.service.ChatService;
import com.example.chatserver.chat.service.RedisPubSubService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class StompController {
    private final SimpMessageSendingOperations messageTemplate;
    private final ChatService chatService;
    private final RedisPubSubService redisPubSubService;

    public StompController(SimpMessageSendingOperations messageTemplate, ChatService chatService,
		RedisPubSubService redisPubSubService) {
        this.messageTemplate = messageTemplate;
		this.chatService = chatService;
		this.redisPubSubService = redisPubSubService;
	}
    // 방법1. MessageMapping(수신) 과 SendTo(topic에 메시지 전달) 어노테이션을 한꺼번에 처리.
    // @MessageMapping("/{roomId}") // 클라이언트에서 특정 publish/roomId 형태로 메시지 발행시 MessageMapping 수신
    // @SendTo("/topic/{roomId}")  // 해당 roomId에 메시지를 발생하여 구독중인 클라이언트에게 메시지 정송
    // // DestinationVariable: @MessageMapping 어노테이션으로 정의된 Websocket Controller 내에서만 사용
    // public String sendMessage(@DestinationVariable Long roomId, String message) {
    //     System.out.println(message);
    //     return message;
    // }

    // 방법2. MessageMapping 어노테이션만 활용.
    @MessageMapping("/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) throws
		JsonProcessingException {
        System.out.println(chatMessageDto.getMessage());
        chatService.saveMessage(roomId, chatMessageDto);
        chatMessageDto.setRoomId(roomId);
		// chatMessageDto.setCreatedTime(LocalDateTime.now());
        // messageTemplate.convertAndSend("/topic/" + roomId, chatMessageRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.writeValueAsString(chatMessageDto);
        redisPubSubService.publish("chat", message);

    }



}
