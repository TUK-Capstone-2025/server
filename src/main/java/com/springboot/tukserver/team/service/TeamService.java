package com.springboot.tukserver.team.service;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberStatus;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.dto.TeamRequest;
import com.springboot.tukserver.team.dto.TeamResponse;
import com.springboot.tukserver.team.repository.TeamRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Team createTeam(TeamRequest request) {
        // ⛳ 토큰에서 인증된 유저 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String leaderUserId = auth.getName(); // userId

        // 리더 멤버 조회
        Member leader = memberRepository.findByUserId(leaderUserId)
                .orElseThrow(() -> new RuntimeException("리더를 찾을 수 없습니다."));

        // 팀 객체 생성
        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setLeader(leaderUserId);
        team.setMemberCount(1); // ✅ 생성 시 리더 포함

        // 멤버와 팀 연관관계 설정
        leader.setTeam(team);
        leader.setStatus(MemberStatus.APPROVE); // 상태도 설정
        team.setMembers(new ArrayList<>(List.of(leader))); // ✅ 멤버 리스트 초기화 및 추가

        teamRepository.save(team);
        memberRepository.save(leader); // 연관 멤버 저장

        return team;
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamDetail(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        List<TeamResponse.MemberSimpleDto> members = team.getMembers().stream()
                .map(member -> TeamResponse.MemberSimpleDto.builder()
                        .userId(member.getUserId())
                        .name(member.getName())
                        .nickname(member.getNickname())
                        .build())
                .toList();

        return TeamResponse.builder()
                .teamId(team.getTeamId())
                .name(team.getName())
                .leader(team.getLeader())
                .description(team.getDescription())
                .memberCount(team.getMemberCount())
                .members(members)
                .build();
    }
}
