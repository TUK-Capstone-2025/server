package com.springboot.tukserver.team.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberDto {

    private String userId;
    private String name;
    private String nickname;

}
