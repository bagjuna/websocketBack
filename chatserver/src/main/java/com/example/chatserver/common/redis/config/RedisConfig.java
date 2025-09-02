package com.example.chatserver.common.redis.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.example.chatserver.chat.service.RedisPubSubService;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String host;

	@Value("${spring.redis.port}")
	private int port;

	// 연결 기본 객체
	@Bean
	@Qualifier("chatPubSub")
	public RedisConnectionFactory chatPubSubFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
		configuration.setHostName(host);
		configuration.setPort(port);
		// redis pub/sub에서는 특정 데이터베이스에 의족적이지 않음
		// configuration.setDatabase(5);
		return new LettuceConnectionFactory(configuration);
	}

	// publish 객체
	@Bean
	@Qualifier("chatPubSub")
	// 일반적으로 RedisTemplate<Key, value> 형태로 많이 사용
	public StringRedisTemplate stringRedisTemplate(@Qualifier("chatPubSub") RedisConnectionFactory chatPubSubFactory) {
		return new StringRedisTemplate(chatPubSubFactory);
	}

	// subscribe 객체
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(
		@Qualifier("chatPubSub") RedisConnectionFactory redisConnectionFactory,
		MessageListenerAdapter messageListenerAdapter
	) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.addMessageListener(messageListenerAdapter, new PatternTopic("chat"));
		return container;

	}

	// redis에서 수신된 메시지를 처리하는 객체 생성
	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisPubSubService redisPubSubService) {
		//  RedisPubSubService의 특정 메서드가 수신된 메시지를 처리 수 있도록 지저
		return new MessageListenerAdapter(redisPubSubService, "OnMessage");

	}

}
