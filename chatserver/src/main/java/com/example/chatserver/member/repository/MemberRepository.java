package com.example.chatserver.member.repository;


import com.example.chatserver.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);


}
