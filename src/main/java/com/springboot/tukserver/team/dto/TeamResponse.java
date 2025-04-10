package com.springboot.tukserver.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class TeamResponse {

    private Long teamId;
    private String name;
    private String leader;
    private String description;
    private int memberCount;
    private List<MemberSimpleDto> members;

    @Data
    @Builder
    @AllArgsConstructor
    public static class MemberSimpleDto {
        private long memberId;
        private String name;
        private String nickname;
    }

}
