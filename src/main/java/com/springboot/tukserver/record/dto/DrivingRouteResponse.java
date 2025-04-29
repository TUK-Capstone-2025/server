package com.springboot.tukserver.record.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DrivingRouteResponse {

    private String startTime;

    private String endTime;

    private List<RoutePoint> route;

}
