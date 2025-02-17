package com.springboot.tukserver.team.domain;

import com.springboot.tukserver.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    private String name;
    private String leader;
    private Integer memberCount;

    @OneToMany(mappedBy = "team")
    private List<Member> members;
}
