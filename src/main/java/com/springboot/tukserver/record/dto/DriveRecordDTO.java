package com.springboot.tukserver.record.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class DriveRecordDTO {

    private Long recordId;
    private String startTime;

}
