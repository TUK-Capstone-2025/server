package com.springboot.tukserver.record.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherRouteResponse {

    private String startTime;
    private String endTime;
    private List<RoutePoint> route;

    @Getter
    @Setter
    @Builder
    public static class RoutePoint {
        private double latitude;
        private double longitude;
        private int warning;
    }

}
