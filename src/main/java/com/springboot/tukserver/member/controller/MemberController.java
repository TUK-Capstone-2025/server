package com.springboot.tukserver.member.controller;

import com.springboot.tukserver.member.dto.RegisterRequest;
import com.springboot.tukserver.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "member/registerForm";
    }

    @PostMapping("/register")
    public String register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {

        try {
            if (!bindingResult.hasErrors()) {
                memberService.registerMember(registerRequest.getUserId(), registerRequest.getPassword(),
                        registerRequest.getEmail(), registerRequest.getName(), registerRequest.getNickname());
            }

        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.rejectValue("userId", "duplicateUserId", "중복된 아이디입니다.");
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.rejectValue("userId", e.getMessage());
        }

        if (!registerRequest.getPassword().equals(registerRequest.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordIncorrect", "패스워드가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> System.out.println("Error: " + error.getDefaultMessage()));
            return "member/registerForm";
        }

        return "redirect:/";


    }

    @GetMapping("/login")
    public String login () {
        return "member/loginForm";
    }

}
