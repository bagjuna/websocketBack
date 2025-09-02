package com.example.chatserver.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.chatserver.chat.domain.ChatParticipant;
import com.example.chatserver.chat.domain.ChatRoom;
import com.example.chatserver.member.domain.Member;

@Repository
public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    List<ChatParticipant> findByChatRoom(ChatRoom chatRoom);

    List<ChatParticipant> findAllByMember(Member member);

    Optional<ChatParticipant> findByChatRoomAndMember(ChatRoom chatRoom, Member member);

    @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 " +
            "ON cp1.chatRoom.id =cp2.chatRoom.id " +
            "WHERE cp1.member.memberId = :myId AND cp2.member.memberId = :otherMemberId " +
            "AND cp1.chatRoom.isGroupChat = 'N'")
    Optional<ChatRoom> findChatRoomIdExistingPrivateRoom(@Param("myId") Long myId, @Param("otherMemberId") Long otherMemberId);

    @Query("SELECT cp1.chatRoom FROM ChatParticipant cp1 JOIN ChatParticipant cp2 " +
        "ON cp1.chatRoom.id =cp2.chatRoom.id " +
        "WHERE cp1.member.memberId = :myId AND cp2.member.memberId = :otherMemberId " +
        "AND cp1.chatRoom.isGroupChat = 'N' "+
        "AND cp1.chatRoom.owner.memberId = :myId")
    Optional<ChatRoom> findExistingPrivateRoomByOwenerId(@Param("myId") Long myId, @Param("otherMemberId") Long otherMemberId);

}
