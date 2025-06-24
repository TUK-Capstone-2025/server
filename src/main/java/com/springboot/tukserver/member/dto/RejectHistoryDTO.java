package com.springboot.tukserver.member.dto;

import com.springboot.tukserver.member.domain.MemberStatus;

import java.time.LocalDateTime;

public record RejectHistoryDTO (
    Long teamId,
    String teamName,
    MemberStatus status
) {}

