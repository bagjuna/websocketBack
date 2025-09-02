package com.example.chatserver.chat.domain;

import java.util.ArrayList;
import java.util.List;

import com.example.chatserver.common.domain.BaseTimeEntity;
import com.example.chatserver.member.domain.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoom extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chatRoomId;

	@Column(nullable = false)
	private String name;

	@Builder.Default
	private String isGroupChat = "N"; // 기본값 설정

	@OneToOne
	@JoinColumn(name = "owener_id", unique = true)
	private Member owner;

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
	private List<ChatParticipant> chatParticipants = new ArrayList<>();

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ChatMessage> chatMessages = new ArrayList<>();


}
