package com.example.chatserver.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.chatserver.chat.domain.ChatMessage;
import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.chat.domain.ReadStatus;
import com.example.chatserver.chat.dto.ChatMessageDto;
import com.example.chatserver.chat.dto.ChatMessageResponse;
import com.example.chatserver.chat.dto.ChatRoomListResponse;
import com.example.chatserver.chat.dto.MyChatListResponse;
import com.example.chatserver.chat.repository.ChatMessageRepository;
import com.example.chatserver.chat.repository.ChatParticipantRepository;
import com.example.chatserver.chat.repository.ChatRoomRepository;
import com.example.chatserver.chat.repository.ReadStatusRepository;
import com.example.chatserver.member.domain.Member;
import com.example.chatserver.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ChatService {

	private final ChatRoomRepository chatRoomRepository;

	private final ChatMessageRepository chatMessageRepository;

	private final ChatParticipantRepository chatParticipantRepository;

	private final ReadStatusRepository readStatusRepository;

	private final MemberRepository memberRepository;

	public ChatService(ChatRoomRepository chatRoomRepository, ChatMessageRepository chatMessageRepository,
		ChatParticipantRepository chatParticipantRepository, ReadStatusRepository readStatusRepository,
		MemberRepository memberRepository) {
		this.chatRoomRepository = chatRoomRepository;
		this.chatMessageRepository = chatMessageRepository;
		this.chatParticipantRepository = chatParticipantRepository;
		this.readStatusRepository = readStatusRepository;
		this.memberRepository = memberRepository;
	}

	public void saveMessage(Long roomId, ChatMessageDto chatMessageDtoRequest) {
		// 1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new EntityNotFoundException("room cannot found. id: ")
		);
		// 2. 보낸 사람 조회
		Member sender = memberRepository.findByEmail(chatMessageDtoRequest.getSenderEmail()).orElseThrow(
			() -> new EntityNotFoundException("sender cannot found. email: ")
		);
		// 3. 채팅 메시지 저장
		ChatMessage chatMessage = ChatMessage.builder()
			.chatRoom(chatRoom)
			.member(sender)
			.content(chatMessageDtoRequest.getMessage())
			.build();
		chatMessageRepository.save(chatMessage);
		// 4. 사용자 별로 읽음 여부 저장
		List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
		for (ChatParticipant c : chatParticipants) {
			ReadStatus readStatus = ReadStatus.builder()
				.chatRoom(c.getChatRoom())
				.member(c.getMember())
				.chatMessage(chatMessage)
				.isRead(c.getMember().equals(sender))
				.build();
			readStatusRepository.save(readStatus);
		}

	}

	public void createGroupChatRoom(String roomName) {
		Member member = memberRepository
			.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
			.orElseThrow(EntityNotFoundException::new);

		// 1. 채팅방 생성
		ChatRoom chatRoom = ChatRoom.builder()
			.name(roomName)
			.isGroupChat("Y")
			.build();
		chatRoomRepository.save(chatRoom);
		// 2. 채팅방 참여자 추가 (현재 로그인한 사용자)
		ChatParticipant chatParticipant = ChatParticipant.builder()
			.chatRoom(chatRoom)
			.member(member)
			.build();

		chatParticipantRepository.save(chatParticipant);

	}

	/**
	 * 전체 그룹채팅방 목록 조회
	 * @return List<ChatRoomListResponse>
	 */
	public List<ChatRoomListResponse> getAllGroupChatRooms() {
		List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
		List<ChatRoomListResponse> chatRoomDtos = new ArrayList<>();
		for (ChatRoom c : chatRooms) {
			ChatRoomListResponse dto = ChatRoomListResponse.builder()
				.roomId(c.getChatRoomId())
				.roomName(c.getName())
				.build();
			chatRoomDtos.add(dto);
		}
		return chatRoomDtos;
	}

	/**
	 * 자신이 속한 그룹채팅방 목록 조회
	 * @return List<ChatRoomListResponse>
	 */
	public List<MyChatListResponse> getMyChatRooms() {
		// 1. 현재 로그인한 사용자 조회
		Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(EntityNotFoundException::new);
		// 2. 참여중인 그룹채팅방 조회
		List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);

		// 3. DTO로 변환
		List<MyChatListResponse> myChatListResponses = new ArrayList<>();
		for (ChatParticipant c : chatParticipants) {
			Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
			MyChatListResponse dto = MyChatListResponse.builder()
				.roomId(c.getChatRoom().getChatRoomId())
				.roomName(c.getChatRoom().getName())
				.isGroupChat(c.getChatRoom().getIsGroupChat())
				.unReadCount(count)
				.build();
			myChatListResponses.add(dto);
		}

		return myChatListResponses;

	}

	public void addParticipantToGroupChat(Long roomId) {
		// 1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(
			() -> new EntityNotFoundException("room cannot found. id: " + roomId)
		);

	  SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		// 2. 현재 로그인한 사용자 조회
		Member member = memberRepository
			.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
			.orElseThrow(EntityNotFoundException::new);

		// 3. 단체 채팅방 여부 체크
		if(chatRoom.getIsGroupChat().equals("N")) {
			throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
		}

		// 4. 중복 참여자 체크
		Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);

		if (!participant.isPresent()) {
			addParticipantToRoom(chatRoom, member);
		}


	}

	// ChatParticipant 엔티티 생성 및 저장

	public void addParticipantToRoom(ChatRoom chatRoom, Member member) {
		ChatParticipant chatParticipant = ChatParticipant.builder()
			.chatRoom(chatRoom)
			.member(member)
			.build();

		chatParticipantRepository.save(chatParticipant);
	}

	/**
	 * 채팅방의 채팅내역 조회
	 * @param roomId
	 * @return
	 */
	public List<ChatMessageResponse> getChatHistory(Long roomId) {
		// 1. 해당 채팅방의 참여자가 아닐경우 예외처리
		// 1-1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("room cannot found. id: " + roomId));
		// 1-2. 현재 로그인한 사용자 조회
		Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(EntityNotFoundException::new);
		// 1-3. 참여자 여부 체크
		List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
		boolean check = false;
		for (ChatParticipant c : chatParticipants) {
			if (c.getMember().equals(member)) {
				check = true;
			}
		}
		if (!check) {
			throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다." );
		}

		// 2. 특정 room에 대한 message 조회
		List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedTimeAsc(chatRoom);
		List<ChatMessageResponse> chatMessageResponses = new ArrayList<>();
		for (ChatMessage c : chatMessages) {
			ChatMessageResponse dto = ChatMessageResponse.builder()
				.message(c.getContent())
				.senderEmail(c.getMember().getEmail())
				.build();

			chatMessageResponses.add(dto);
		}

		return chatMessageResponses;

	}

	/**
	 * 특정 채팅방에 사용자가 참여자인지 체크
	 * @param email
	 * @param roomId
	 * @return
	 */
	public boolean isRoomParticipant(String email, Long roomId) {
		// 해당 roomId에 email을 가진 사용자가 참여자인지 체크

		// 1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("room cannot found. id: " + roomId));
		// 2. 현재 로그인한 사용자 조회
		Member member = memberRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

		// 3. 참여자 여부 체크
		List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
		for (ChatParticipant c : chatParticipants) {
			if (c.getMember().equals(member)) {
				return true;
			}
		}
		return false;
	}

	public void messageRead(Long roomId) {
		// 1. 해당 채팅방의 참여자가 아닐경우 예외처리
		// 1.1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("room cannot found. id: " + roomId));
		// 1.2. 현재 로그인한 사용자 조회
		Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(EntityNotFoundException::new);
		// 2.1. 메시지 읽음 여부 조회
		List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
		// 2.2. 읽음 처리
		for (ReadStatus r : readStatuses) {
			r.updateReadStatus(true);
		}

	}

	// del Y/N
	// 1. 참여자 객체 삭제
	// 2. 모두가 나갔을 경우 모든 엔티티 삭제
	public void leaveChatRoom(Long roomId) {
		// 1.1. 채팅방 조회
		ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("room cannot found. id: " + roomId));
		// 1.2. 현재 로그인한 사용자 조회
		Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(EntityNotFoundException::new);

		if(chatRoom.getIsGroupChat().equals("N")) {
			throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
		}

		ChatParticipant c = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(() -> new EntityNotFoundException("participant cannot found. roomId: " + roomId + ", memberId: " + member.getMemberId()));
		chatParticipantRepository.delete(c);

		List<ChatParticipant> participants = chatParticipantRepository.findByChatRoom(chatRoom);
		if (participants.isEmpty()) {
			chatRoomRepository.delete(chatRoom);
		}

	}

	public Long getOrCreatePrivateChatRoom(String otherMemberId) {
		// 1-1. 현재 로그인한 사용자 조회
		Member member = memberRepository
			.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
			.orElseThrow(EntityNotFoundException::new);

		// 1.2. 상대방 사용자 조회
		Member otherMember = memberRepository
			.findById(Long.parseLong(otherMemberId)).orElseThrow(() -> new EntityNotFoundException("member cannot found. id: " + otherMemberId));

		// 2. 상대방이 1:1 채팅에 이미 참여하고 있다면 roomId return
		Optional<ChatRoom> chatRoom = chatParticipantRepository.findChatRoomIdExistingPrivateRoom(member.getMemberId(), otherMember.getMemberId());
		if (chatRoom.isPresent()) {
			return chatRoom.get().getChatRoomId();
		}
		// 3. 만약 1:1 채팅방이 없다면 새로 생성
		ChatRoom newChatRoom = ChatRoom.builder()
			.isGroupChat("N")
			.name(member.getName() + "-" + otherMember.getName())
			.owner(member)
			.build();
		chatRoomRepository.save(newChatRoom);

		// 4. 두사람 모두 참여자로 추가
		addParticipantToRoom(newChatRoom, member);
		addParticipantToRoom(newChatRoom, otherMember);
		return newChatRoom.getChatRoomId();
	}
}
