package com.springboot.tukserver.member.controller;

import com.springboot.tukserver.ApiResponse;
import com.springboot.tukserver.JwtUtil;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberStatus;
import com.springboot.tukserver.member.dto.*;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.member.service.MemberService;
import com.springboot.tukserver.security.CustomUserDetails;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.domain.TeamApplicationHistory;
import com.springboot.tukserver.team.dto.TeamApplicationResponse;
import com.springboot.tukserver.team.repository.TeamApplicationHistoryRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private JwtUtil jwtUtil;

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final MemberRepository memberRepository;
    private final TeamApplicationHistoryRepository teamApplicationHistoryRepository;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "입력값이 올바르지 않습니다.", null));
            }

            if (!registerRequest.getPassword().equals(registerRequest.getPassword2())) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "비밀번호가 일치하지 않습니다.", null));
            }

            memberService.registerMember(
                    registerRequest.getUserId(),
                    registerRequest.getPassword(),
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getNickname()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "회원가입 성공", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "회원가입 실패: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest, HttpSession session) {

        System.out.println("🔐 로그인 시도: userId = " + loginRequest.getUserId());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserId(), loginRequest.getPassword()
                    )
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // ✅ 관리자 로그인 시 세션에 SecurityContext 저장
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
                return ResponseEntity.ok(new ApiResponse<>(true, "관리자 로그인 성공", "/admin/dashboard"));
            }

            String token = jwtUtil.generateToken(loginRequest.getUserId()); // ✨ JWT 사용 시 필요
            return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "로그인 실패: " + e.getMessage(), null));
        }}


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails customUser = (CustomUserDetails) principal;
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("userId", customUser.getUsername());
            userInfo.put("name", customUser.getName());
            userInfo.put("nickname", customUser.getNickname());
            userInfo.put("profileImageUrl", customUser.getProfileImageUrl());
                return ResponseEntity.ok(new ApiResponse<>(true, "현재 로그인된 사용자", userInfo));
            }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "로그인되지 않음", null));
    }

    // ✅ 멤버를 특정 팀에 배정하는 API 추가
    @PostMapping("/{memberId}/assignTeam/{teamId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> assignMemberToTeam(
            @PathVariable Long memberId,
            @PathVariable Long teamId) {

        // ✅ `memberId`를 이용하여 사용자 정보 조회
        Member member = memberService.findMemberById(memberId);
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "해당 사용자를 찾을 수 없습니다.", null));
        }

        memberService.assignToTeam(memberId);

        // ✅ JSON 응답 데이터 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", member.getUserId());  // 사용자가 설정한 로그인 ID
        responseData.put("teamId", teamId);

        return ResponseEntity.ok(new ApiResponse<>(true, "멤버가 팀에 정상적으로 배정되었습니다!", responseData));
    }

    @PostMapping("/changePass")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordRequest request) {

        try {
            // ✅ 비밀번호 변경 로직 실행
            memberService.changePassword(request.getCurrentPassword(), request.getNewPassword());

            return ResponseEntity.ok(new ApiResponse<>(true, "비밀번호가 성공적으로 변경되었습니다.", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/changeNick")
    public ResponseEntity<ApiResponse<Void>> changeNickname(@Valid @RequestBody NicknameRequest request) {

        try {
            // ✅ 닉네임 변경 로직 실행
            memberService.changeNickname(request.getNewNickname());

            return ResponseEntity.ok(new ApiResponse<>(true, "닉네임이 성공적으로 변경되었습니다.", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/changeId")
    public ResponseEntity<ApiResponse<Void>> changeUserId(
            @Valid @RequestBody UserIdRequest request) {

        try {
            // ✅ 아이디 변경 로직 실행
            memberService.changeUserId(request.getNewUserId());

            return ResponseEntity.ok(new ApiResponse<>(true, "아이디가 성공적으로 변경되었습니다.", null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    @GetMapping("/team")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyTeam() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "인증 정보가 없습니다.", null));
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "유효한 사용자 정보가 없습니다.", null));
        }

        Member member = memberRepository.findByUserId(customUser.getUsername())
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        Map<String, Object> result = new HashMap<>();

        Team team = member.getTeam();
        if (team == null || member.getStatus() != MemberStatus.APPROVE) {
            result.put("isInTeam", false);
            result.put("teamId", null);
            return ResponseEntity.ok(new ApiResponse<>(true, "팀에 속해있지 않습니다.", result));
        }

        result.put("isInTeam", true);
        result.put("teamId", team.getTeamId());

        return ResponseEntity.ok(new ApiResponse<>(true, "팀 정보 조회 성공", result));
    }

    @PostMapping("/applyTeam/{teamId}")
    public ResponseEntity<ApiResponse<MemberStatus>> applyToTeamWithToken(@PathVariable Long teamId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "인증 정보가 없습니다.", null));
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "유효한 사용자 정보가 없습니다.", null));
        }

        // 🔍 토큰에서 userId 추출 후 member 조회
        Member member = memberRepository.findByUserId(customUser.getUsername())
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        // ✅ 팀 신청 로직 수행
        memberService.applyToTeam(member.getMemberId(), teamId);

        return ResponseEntity.ok(new ApiResponse<>(true, "팀 신청 완료(대기 중).", MemberStatus.PENDING));
    }

    @GetMapping("/listMembers")
    public ResponseEntity<ApiResponse<List<MemberSimpleDTO>>> getPendingMembersForLeader() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String leaderUserId = auth.getName();

        List<MemberSimpleDTO> pending = memberService.findPendingMembersByLeader(leaderUserId);
        return ResponseEntity.ok(new ApiResponse<>(true, "승인 대기 중인 멤버 목록", pending));
    }


    @GetMapping("/applyStatus")
    public ResponseEntity<ApiResponse<List<TeamApplicationResponse>>> getMyTeamApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "인증 정보가 없습니다.", null));
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails customUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "유효한 사용자 정보가 없습니다.", null));
        }

        // 🔍 현재 로그인된 사용자에서 userId → memberId 조회
        String userId = customUser.getUsername();
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        List<TeamApplicationResponse> applications = memberService.getTeamApplications(member.getMemberId());

        return ResponseEntity.ok(new ApiResponse<>(true, "팀 신청 상태 조회 성공", applications));
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelTeamApplication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "인증 정보가 없습니다.", null));
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails customUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "유효한 사용자 정보가 없습니다.", null));
        }

        Member member = memberRepository.findByUserId(customUser.getUsername())
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        memberService.cancelTeamApplication(member.getMemberId());

        return ResponseEntity.ok(new ApiResponse<>(true, "팀 신청이 성공적으로 취소되었습니다.", null));
    }

    @PostMapping("/uploadProfile")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 저장 경로를 static/images/profile 안으로 지정
            String uploadDir = new File("uploads/profile").getAbsolutePath();
            File folder = new File(uploadDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            File dest = new File(uploadDir + "/" + fileName);
            file.transferTo(dest);
            String imageUrl = "https://f9237630f53d.ngrok-free.app/images/profile/" + fileName;

            // 토큰 기반 사용자 식별
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("사용자 없음"));

            member.setProfileImageUrl(imageUrl);
            memberRepository.save(member);

            return ResponseEntity.ok(new ApiResponse<>(true, "프로필 이미지 등록 성공", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "업로드 실패: " + e.getMessage(), null));
        }
    }

    @GetMapping("/rejectList")
    public ResponseEntity<ApiResponse<List<RejectHistoryDTO>>> getRejectList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<RejectHistoryDTO> rejects = memberService.getLatestRejects(userDetails.getMemberId());
        return ResponseEntity.ok(new ApiResponse<>(true, "거절된 팀 신청 내역 조회 성공", rejects));
    }

}
