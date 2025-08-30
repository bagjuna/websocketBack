package com.example.chatserver.member.domain;

import com.example.chatserver.common.domain.BaseTimeEntity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER; // 기본값 직접 할당


}
