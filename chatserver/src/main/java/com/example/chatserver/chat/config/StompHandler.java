package com.example.chatserver.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.support.ChannelInterceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

// 인증 작업
@Component
public class StompHandler implements ChannelInterceptor {

	@Value("${jwt.secretKey}")
	private String secretKey;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		final StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			System.out.println("connect요청시 토큰 유효성 검증");
			String bearerToken = accessor.getFirstNativeHeader("Authorization");
			String token = bearerToken.substring(7);
			// 토큰 검증 및 claims 추출
			Claims claims = Jwts.parser()
					.setSigningKey(secretKey)
					.parseClaimsJws(token)
					.getBody();
			System.out.println("토큰 검증 완료");
		}

		return message;
	}

}
