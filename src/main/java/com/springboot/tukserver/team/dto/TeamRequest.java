package com.springboot.tukserver.team.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequest {

    private String name;
    private String leaderUserId;
    private String description;

}
