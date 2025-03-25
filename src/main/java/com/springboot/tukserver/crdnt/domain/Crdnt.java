package com.springboot.tukserver.crdnt.domain;

import com.springboot.tukserver.record.domain.DriveRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Crdnt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    @Column
    private Long x;
    private Long y;

    @ManyToOne
    @JoinColumn(name = "recordId")
    private DriveRecord driveRecord;

}
