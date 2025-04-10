package com.springboot.tukserver.crdnt.domain;

import com.springboot.tukserver.record.domain.DriveRecord;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Crdnt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    @Column
    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private Integer accidentStatus = 0;

    @ManyToOne
    @JoinColumn(name = "recordId")
    private DriveRecord driveRecord;



}
