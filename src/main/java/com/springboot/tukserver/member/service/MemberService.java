package com.springboot.tukserver.member.service;

import com.springboot.tukserver.member.dto.LoginRequest;
import com.springboot.tukserver.member.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.dto.MemberRegisterRequest;
import com.springboot.tukserver.member.dto.MemberRegisterResponse;
import com.springboot.tukserver.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberRegisterResponse register(MemberRegisterRequest request){
        duplicationUserId(request.userId());
        Member member = Member.builder()
                .userId(request.userId())
                .password(request.password())
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .nickname(request.nickname())
                .build();
        memberRepository.save(member);
        return new MemberRegisterResponse(member.getId());
    }

    private void duplicationUserId(String userId){
        if(memberRepository.existsByUserId(userId)){

        }
    }

    public LoginResponse login(LoginRequest request){
        Member member = null;
        try {
            member = memberRepository.findByUserId(request.userId())
                    .orElseThrow(() -> new LoginException("아이디가 일치하지 않습니다"));
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
        return new LoginResponse(member.getId());
    }


}

