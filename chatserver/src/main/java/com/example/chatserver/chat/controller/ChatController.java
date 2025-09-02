package com.example.chatserver.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatserver.chat.dto.ChatMessageResponse;
import com.example.chatserver.chat.dto.ChatRoomListResponse;
import com.example.chatserver.chat.dto.MyChatListResponse;
import com.example.chatserver.chat.service.ChatService;


@RestController
@RequestMapping("/chat")
public class ChatController {
	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	// 그룹 채팅방 개설
	@PostMapping("/room/group/create")
	public ResponseEntity<?> createChatRoom(@RequestParam String roomName) {
		chatService.createGroupChatRoom(roomName);
		return ResponseEntity.ok().build();
	}

	// 1:1 채팅방 개설
	@PostMapping("/room/private/create")
	public ResponseEntity<?> getOrCreatePrivateChatRoom(@RequestParam String otherMemberId) {
		Long roomId = chatService.getOrCreatePrivateChatRoom(otherMemberId);
		return ResponseEntity.ok().body(roomId);
	}

	// 그룹채팅목록조회
	@GetMapping("/room/group/list")
	public ResponseEntity<?> getGroupChatRooms() {
		List<ChatRoomListResponse> chatRooms = chatService.getAllGroupChatRooms();
		return ResponseEntity.ok(chatRooms);
	}

	// 그룹 채팅방 참여
	@PostMapping("/room/group/{roomId}/join")
	public ResponseEntity<?> joinGroupChatRoom(@PathVariable Long roomId) {
		chatService.addParticipantToGroupChat(roomId);
		return ResponseEntity.ok().build();
	}

	// 이전 메시지 조회
	@GetMapping("/history/{roomId}")
	public ResponseEntity<?> getChatHistory(@PathVariable Long roomId) {
		List<ChatMessageResponse> chatHistory = chatService.getChatHistory(roomId);
		return ResponseEntity.ok(chatHistory);
	}

	// 채팅 메시지 읽음 처리
	@PostMapping("/room/{roomId}/read")
	public ResponseEntity<?> messageRead(@PathVariable Long roomId) {
		chatService.messageRead(roomId);
		return ResponseEntity.ok().build();
	}

	// 자신이 속한 그룹채팅목록조회 roomId, 그룹채팅여부, 메시지읽음개수
	@GetMapping("/my/rooms")
	public ResponseEntity<?> getMyChatRooms() {
		List<MyChatListResponse> chatRooms = chatService.getMyChatRooms();
		return ResponseEntity.ok(chatRooms);
	}

	@DeleteMapping("/room/{roomId}/leave")
	public ResponseEntity<?> leaveChatRoom(@PathVariable Long roomId) {
		chatService.leaveChatRoom(roomId);
		return ResponseEntity.ok().build();
	}


}
