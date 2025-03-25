package com.springboot.tukserver.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecordResponse {

    private Long id;
    private LocalDateTime startTime;

    public RecordResponse(Long id, LocalDateTime startTime) {
        this.id = id;
        this.startTime = startTime;
    }

}
