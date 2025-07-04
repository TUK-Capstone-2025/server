package com.springboot.tukserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberProfileResponse {

    private String nickname;
    private String profileImageUrl;
    private double totalDistance;

}
