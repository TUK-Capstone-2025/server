package com.springboot.tukserver.record.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoutePoint {

    private double latitude;
    private double longitude;
    private int warning;

}
