package com.springboot.tukserver.crdnt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DrivingEndRequest {

    private String startTime;
    private String endTime;
    private List<RoutePoint> route; // 경로 데이터

    @Data
    public static class RoutePoint {
        private double latitude;
        private double longitude;
        private int warning; // 0: 사고없음, 1: 후방감지, 2: 쓰러짐
    }

}
