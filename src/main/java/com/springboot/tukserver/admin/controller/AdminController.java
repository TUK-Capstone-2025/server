package com.springboot.tukserver.admin.controller;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberRole;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.repository.TeamRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

    public AdminController(MemberRepository memberRepository, TeamRepository teamRepository) {
        this.memberRepository = memberRepository;
        this.teamRepository = teamRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        // ✅ SecurityContext에서 관리자 정보 가져오기
        SecurityContext securityContext = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

        if (securityContext == null || securityContext.getAuthentication() == null) {
            return "redirect:/member/login"; // ✅ 세션이 없으면 로그인 페이지로 이동
        }

        UserDetails adminUser = (UserDetails) securityContext.getAuthentication().getPrincipal();
        model.addAttribute("adminUser", adminUser);

        return "admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/login";
    }
}
