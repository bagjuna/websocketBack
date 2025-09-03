package com.example.chatserver.chat.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.example.chatserver.chat.dto.ChatMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RedisPubSubService implements MessageListener {

	private final StringRedisTemplate stringRedisTemplate;

	private final SimpMessageSendingOperations messageTemplate;

	public RedisPubSubService(@Qualifier("chatPubSub") StringRedisTemplate stringRedisTemplate, SimpMessageSendingOperations messageTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.messageTemplate = messageTemplate;
	}

	// publish 메서드: 특정 채널에 메시지를 발행하는 역할
	public void publish(String channel, String message) {
		// Redis에서 수신된 메시지를 처리하는 로직 작성
		stringRedisTemplate.convertAndSend(channel, message);

	}



	@Override
	// pattern에는 topic 이름의 패턴이 담겨있고, 이 패턴을 기반으로 다이나믹한 코딩 가능
	// 여기서는 사용하지 않음
	public void onMessage(Message message, byte[] pattern) {
		String payload = new String(message.getBody());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
			messageTemplate.convertAndSend("/topic/" + chatMessageDto.getRoomId(), chatMessageDto);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}
}
