package com.springboot.tukserver.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberDistanceDTO {

    private long memberId;
    private String userId;
    private String nickname;
    private double totalDistance;

}
