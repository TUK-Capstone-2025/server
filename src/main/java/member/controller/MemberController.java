package member.controller;

import lombok.RequiredArgsConstructor;
import member.dto.MemberRegisterRequest;
import member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/register")
    public String register(MemberRegisterRequest registerRequest) {
        try{
            memberService.register(registerRequest);
        } catch (Exception e) {
            return "duplicationUserId";
        }
        return "redirect:/";
    }

    @GetMapping("/register-form")
    public String registerForm() {return "join";}

}
