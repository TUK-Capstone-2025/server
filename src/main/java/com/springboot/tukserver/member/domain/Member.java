package com.springboot.tukserver.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String password;
    private String nickname;
    private String name;
    private String email;
    private String phone;

    @Builder
    public Member(String userId, String password, String nickname, String name, String email, String phone) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
