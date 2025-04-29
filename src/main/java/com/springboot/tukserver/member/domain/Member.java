package com.springboot.tukserver.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.tukserver.team.domain.Team;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(unique = true)
    private String userId;
    private String password;
    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberStatus status = MemberStatus.NONE;

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.USER;

    @Column(nullable = true)
    private String profileImageUrl;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonIgnore
    private Team team;


}
