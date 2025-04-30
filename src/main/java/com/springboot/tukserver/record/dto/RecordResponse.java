package com.springboot.tukserver.record.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class RecordResponse {

    private Long recordId;
    private String startTime;

    public RecordResponse(Long id, LocalDateTime startTime) {
        this.recordId = id;
        this.startTime = startTime.format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm:ss"));
    }

}
