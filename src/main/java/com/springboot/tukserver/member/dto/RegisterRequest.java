package com.springboot.tukserver.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterRequest {
    @NotEmpty(message = "이름은 필수 항목입니다.")
    private String name;

    @NotEmpty(message = "아이디는 필수 항목입니다.")
    private String userId;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;

    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password2;

    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력하세요.")
    private String email;

    @NotEmpty(message = "닉네임은 필수 항목입니다.")
    private String nickname;

}
