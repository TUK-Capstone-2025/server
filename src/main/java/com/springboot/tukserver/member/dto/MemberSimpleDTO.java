package com.springboot.tukserver.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class MemberSimpleDTO {
    private Long memberId;
    private String userId;
    private String name;
    private String nickname;

}
