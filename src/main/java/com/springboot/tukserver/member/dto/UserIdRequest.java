package com.springboot.tukserver.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIdRequest {

    @NotBlank(message = "새 아이디를 입력하세요.")
    private String newUserId;

}
