package com.springboot.tukserver.record.domain;

import com.springboot.tukserver.member.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class DriveRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    private LocalDateTime startTime;

    @ManyToOne
    @JoinColumn(name = "memberId")
    private Member member;

}
