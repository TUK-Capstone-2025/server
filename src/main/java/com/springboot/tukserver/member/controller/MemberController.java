package com.springboot.tukserver.member.controller;

import com.springboot.tukserver.member.dto.LoginRequest;
import com.springboot.tukserver.member.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import com.springboot.tukserver.member.dto.MemberRegisterRequest;
import com.springboot.tukserver.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }
    @PostMapping("/register")
    public String register(MemberRegisterRequest registerRequest) {
        try{
            memberService.register(registerRequest);
        } catch (Exception e) {
            return "duplicationUserId";
        }
        return "redirect:/";
    }

    // 회원가입 폼 페이지 요청
    @GetMapping("/register")
    public String registerForm() {
        return "member/registerForm";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @PostMapping("/login")
    public String login(LoginRequest loginRequest, HttpServletRequest request,
                        HttpServletResponse response){
        LoginResponse loginResponse = memberService.login(loginRequest);
        HttpSession session = request.getSession();
        session.setAttribute("userId",loginResponse.userId());
        session.setMaxInactiveInterval(36000);
        return "redirect:/";
    }

}
