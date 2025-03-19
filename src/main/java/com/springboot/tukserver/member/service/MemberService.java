package com.springboot.tukserver.member.service;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberRole;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;

    public Member registerMember(String userId, String password, String name, String email, String nickname) {

        Member member = new Member();
        member.setUserId(userId);
        member.setPassword(passwordEncoder.encode(password));
        member.setName(name);
        member.setEmail(email);
        member.setNickname(nickname);
        this.memberRepository.save(member);
        return member;

    }

    public MemberService(MemberRepository memberRepository, TeamRepository teamRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);  // ✅ 존재하지 않으면 `null` 반환
    }


    @Transactional
    public void assignMemberToTeam(Long memberId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        member.setTeam(team);  // 멤버에게 팀을 할당
        memberRepository.save(member);  // 변경사항 저장
    }

    public void changePassword(String currentPassword, String newPassword) {
        // ✅ 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 현재 로그인한 사용자의 아이디 가져오기

        // ✅ DB에서 사용자 정보 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        // ✅ 새 비밀번호 암호화 후 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }

    public void changeNickname(String newNickname) {
        // ✅ 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // 현재 로그인한 사용자의 아이디 가져오기

        // ✅ DB에서 사용자 정보 조회
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // ✅ 새 닉네임 설정 후 저장
        member.setNickname(newNickname);
        memberRepository.save(member);
    }



}
