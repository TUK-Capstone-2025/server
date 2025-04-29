package com.springboot.tukserver.security;

import com.springboot.tukserver.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final Member member;

    public CustomUserDetails(Member member, Collection<? extends GrantedAuthority> authorities) {
        super(member.getUserId(), member.getPassword(), authorities);
        this.member = member;
    }

    public String getName() {
        return member.getName();
    }

    public String getNickname() {
        return member.getNickname();
    }

    public String getProfileImageUrl() {
        return member.getProfileImageUrl();  // ✅ 이제 사용 가능
    }

    public Long getMemberId() {
        return member.getMemberId();
    }

    public Member getMember() {
        return member;
    }
}
