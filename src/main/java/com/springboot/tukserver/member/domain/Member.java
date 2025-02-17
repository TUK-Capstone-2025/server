package com.springboot.tukserver.member.domain;

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

    private String status = "REJECT";

    @Enumerated(EnumType.STRING)
    private MemberRole role = MemberRole.USER;

    @ManyToOne
    @JoinColumn(name = "teamId")
    private Team team;


}
