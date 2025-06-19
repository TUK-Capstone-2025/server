package com.springboot.tukserver.team.domain;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamApplicationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Team team;

    @Enumerated(EnumType.STRING)
    private MemberStatus status; // PENDING, APPROVE, REJECT

    @CreationTimestamp
    private LocalDateTime createdAt;


}
