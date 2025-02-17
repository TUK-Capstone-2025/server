package com.springboot.tukserver.member.controller;

import com.springboot.tukserver.member.dto.LoginRequest;
import com.springboot.tukserver.member.dto.RegisterRequest;
import com.springboot.tukserver.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body("입력값이 올바르지 않습니다.");
            }

            if (!registerRequest.getPassword().equals(registerRequest.getPassword2())) {
                return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
            }

            memberService.registerMember(
                    registerRequest.getUserId(),
                    registerRequest.getPassword(),
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getNickname()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {

        String userId = request.get("userId");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, password)
            );

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            return ResponseEntity.ok("로그인 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            return ResponseEntity.ok(Map.of("userId", ((org.springframework.security.core.userdetails.User) principal).getUsername()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인되지 않음");
    }

    // ✅ 멤버를 특정 팀에 배정하는 API 추가
    @PostMapping("/{memberId}/assignTeam/{teamId}")
    public String assignMemberToTeam(@PathVariable Long memberId, @PathVariable Long teamId) {
        memberService.assignMemberToTeam(memberId, teamId);
        return "멤버가 팀에 정상적으로 배정되었습니다!";
    }


}
