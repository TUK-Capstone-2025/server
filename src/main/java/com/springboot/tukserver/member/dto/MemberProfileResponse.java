package com.springboot.tukserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
public class MemberProfileResponse {

    private String nickname;
    private String profileImageUrl;

}
