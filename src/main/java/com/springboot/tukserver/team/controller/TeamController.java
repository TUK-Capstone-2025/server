package com.springboot.tukserver.team.controller;

import com.springboot.tukserver.ApiResponse;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.dto.MemberProfileResponse;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.member.service.MemberService;
import com.springboot.tukserver.security.CustomUserDetails;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.dto.MemberDto;
import com.springboot.tukserver.team.dto.TeamRequest;
import com.springboot.tukserver.team.dto.TeamResponse;
import com.springboot.tukserver.team.repository.TeamRepository;
import com.springboot.tukserver.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamRepository teamRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createTeam(@RequestBody TeamRequest request) {

        teamService.createTeam(request);

        return ResponseEntity.ok(new ApiResponse<>(true, "팀이 성공적으로 생성되었습니다.", null));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllTeams() {
        List<Team> teams = teamRepository.findAll();

        // 필요한 정보만 추출해서 전달
        List<Map<String, Object>> result = teams.stream().map(team -> {
            Map<String, Object> map = new HashMap<>();
            map.put("teamId", team.getTeamId());
            map.put("name", team.getName());

            // 🔍 리더 userId → 닉네임으로 변환
            String leaderUserId = team.getLeader();
            String leaderNickname = memberRepository.findByUserId(leaderUserId)
                    .map(Member::getNickname)
                    .orElse("알 수 없음"); // 예외 상황 대비

            map.put("leader", leaderNickname);
            map.put("description", team.getDescription());
            map.put("memberCount", team.getMemberCount());
            return map;
        }).toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "팀 목록 조회 성공", result));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<ApiResponse<List<MemberDto>>> getTeamMembers(@PathVariable Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("팀을 찾을 수 없습니다."));

        List<Member> members = team.getMembers();

        // DTO로 변환 (선택사항)
        List<MemberDto> result = members.stream()
                .map(member -> new MemberDto(member.getUserId(), member.getName(), member.getNickname()))
                .toList();

        return ResponseEntity.ok(new ApiResponse<>(true, "팀의 멤버 목록", result));
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<Void>> approveMember(@RequestBody Map<String, Long> request) {
        Long memberId = request.get("memberId");

        String leaderUserId = getCurrentUserIdFromToken();
        memberService.approveMember(memberId, leaderUserId);

        return ResponseEntity.ok(new ApiResponse<>(true, "멤버 승인 완료", null));
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<Void>> rejectMember(@RequestBody Map<String, Long> request) {
        Long memberId = request.get("memberId");

        String leaderUserId = getCurrentUserIdFromToken();
        memberService.rejectMember(memberId, leaderUserId);

        return ResponseEntity.ok(new ApiResponse<>(true, "멤버 거절 완료", null));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamDetail(@PathVariable Long teamId) {
        TeamResponse response = teamService.getTeamDetail(teamId);
        return ResponseEntity.ok(new ApiResponse<>(true, "팀 상세 정보 조회 성공", response));
    }

    // 🔍 토큰에서 로그인한 유저 ID 추출 메서드
    private String getCurrentUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUser) {
            return customUser.getUsername(); // userId
        } else {
            throw new RuntimeException("유효한 사용자 정보가 없습니다.");
        }

    }

    @PostMapping("/kick/{memberId}")
    public ResponseEntity<ApiResponse<Void>> kickMember(
            @PathVariable Long memberId
    ) {
        memberService.kickOutMember(memberId); // 리더 검증은 서비스 내에서
        return ResponseEntity.ok(new ApiResponse<>(true, "멤버가 퇴출되었습니다.", null));
    }

    @GetMapping("/member/profile/{memberId}")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMemberProfile(@PathVariable Long memberId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        MemberProfileResponse response = memberService.getMemberProfile(memberId, userId);

        return ResponseEntity.ok(new ApiResponse<>(true, "멤버 프로필 조회 성공", response));
    }

}
