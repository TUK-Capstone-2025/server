package com.springboot.tukserver.member.controller;

import com.springboot.tukserver.ApiResponse;
import com.springboot.tukserver.JwtUtil;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.dto.LoginRequest;
import com.springboot.tukserver.member.dto.RegisterRequest;
import com.springboot.tukserver.member.service.MemberService;
import com.springboot.tukserver.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private JwtUtil jwtUtil;

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;


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

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUserId(), loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

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

        memberService.assignMemberToTeam(memberId, teamId);

        // ✅ JSON 응답 데이터 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", member.getUserId());  // 사용자가 설정한 로그인 ID
        responseData.put("teamId", teamId);

        return ResponseEntity.ok(new ApiResponse<>(true, "멤버가 팀에 정상적으로 배정되었습니다!", responseData));
    }


}
