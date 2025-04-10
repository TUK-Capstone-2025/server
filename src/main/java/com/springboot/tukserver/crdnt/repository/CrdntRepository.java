package com.springboot.tukserver.crdnt.repository;

import com.springboot.tukserver.crdnt.domain.Crdnt;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.record.domain.DriveRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CrdntRepository extends JpaRepository<Crdnt, Long> {

    // 특정 주행 기록에 연결된 좌표 목록
    List<Crdnt> findByDriveRecord(DriveRecord driveRecord);

    List<Crdnt> findByDriveRecord_RecordId(Long recordId);
}
