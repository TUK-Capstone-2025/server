package com.springboot.tukserver.team.dto;

import com.springboot.tukserver.member.domain.MemberStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TeamApplicationResponse {

    private Long teamId;
    private String teamName;
    private MemberStatus status;

    public TeamApplicationResponse(Long teamId, String teamName, MemberStatus status) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.status = status;
    }

}
