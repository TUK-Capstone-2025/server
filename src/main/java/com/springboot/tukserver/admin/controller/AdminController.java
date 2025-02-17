package com.springboot.tukserver.admin.controller;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberRole;
import com.springboot.tukserver.member.repository.MemberRepository;
import com.springboot.tukserver.team.domain.Team;
import com.springboot.tukserver.team.repository.TeamRepository;
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
    public String adminDashboard(Model model) {

        List<Member> members = memberRepository.findByRoleNot(MemberRole.ADMIN);// 회원 목록 가져오기
        List<Team> teams = teamRepository.findAll(); // 팀 목록 가져오기

        model.addAttribute("members", members);
        model.addAttribute("teams", teams);

        return "admin/dashboard";
    }
}
