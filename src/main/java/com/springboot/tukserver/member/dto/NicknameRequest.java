package com.springboot.tukserver.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NicknameRequest {

    @NotBlank(message = "새 닉네임을 입력하세요.")
    private String newNickname;

}
