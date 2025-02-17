package com.springboot.tukserver.page;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

    @GetMapping("/member/register")
    public String registerPage() {
        return "/member/registerForm";
    }

    @GetMapping("/member/login")
    public String login () {
        return "member/loginForm";
    }



}
