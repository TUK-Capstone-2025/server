package com.springboot.tukserver.team.dto;

import com.springboot.tukserver.member.domain.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamApplicationResponse {

    private Long teamId;
    private String teamName;
    private MemberStatus status;
}
