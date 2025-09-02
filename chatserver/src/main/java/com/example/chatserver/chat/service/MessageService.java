package com.example.chatserver.chat.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

	private final MemberRepository memberRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final SimpMessagingTemplate simpMessagingTemplate;

	public static final String CHAT_DESTINATION_PREFIX = "/topic/";
	public static final String HAS_JOINED = "님이 참가하였습니다.";
	public static final String HAS_LEFT = "님이 나갔습니다.";


	@Transactional
	public void publish(ChatMessage message) {
		simpMessagingTemplate.convertAndSend(CHAT_DESTINATION_PREFIX + message.getChatRoom().getChatRoomId(), message);
		chatMessageRepository.save(message);
	}

	@Transactional
	public void sendChatMessage(Long memberId, Long chatRoomId, String content) {
		Member member = memberRepository.findById(memberId).orElseThrow();
		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();

		ChatMessage chatMessage = ChatMessage.createChatMessage(member, chatRoom, content);

		publish(chatMessage);
	}

	@Transactional
	public void sendSystemMessage(Long memberId, Long chatRoomId, String content) {
		Member member = memberRepository.findById(memberId).orElseThrow();
		ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
		ChatMessage systemMessage = ChatMessage.createSystemMessage(member, chatRoom, content);

		publish(systemMessage);
	}

	@Transactional
	public void sendJoinedMessage(Member member, ChatRoom chatRoom) {
		ChatMessage joinMessage = ChatMessage.createSystemMessage(member, chatRoom, member.getName() + HAS_JOINED);
		publish(joinMessage);
	}

	@Transactional
	public void sendLeftMessage(Member member, ChatRoom chatRoom) {
		ChatMessage leftMessage = ChatMessage.createSystemMessage(member, chatRoom, member.getName() + HAS_LEFT);
		publish(leftMessage);
	}
}
