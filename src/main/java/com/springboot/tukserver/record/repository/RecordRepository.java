package com.springboot.tukserver.record.repository;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.record.domain.DriveRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<DriveRecord, Long> {

    Optional<DriveRecord> findTopByMemberOrderByStartTimeDesc(Member member);
    List<DriveRecord> findAllByMemberOrderByStartTimeDesc(Member member);

}
