package com.springboot.tukserver.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecordResponse {

    private Long recordId;
    private LocalDateTime startTime;

    public RecordResponse(Long id, LocalDateTime startTime) {
        this.recordId = id;
        this.startTime = startTime;
    }

}
