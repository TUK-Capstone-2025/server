package com.springboot.tukserver.record.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherRouteResponse {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
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
