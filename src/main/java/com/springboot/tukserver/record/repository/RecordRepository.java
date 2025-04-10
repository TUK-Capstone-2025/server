package com.springboot.tukserver.record.repository;

import com.springboot.tukserver.crdnt.domain.Crdnt;
import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.record.domain.DriveRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<DriveRecord, Long> {

    List<DriveRecord> findAllByMemberOrderByStartTimeDesc(Member member);
    List<DriveRecord> findByMember_Team_TeamId(Long teamId);
    List<DriveRecord> findByMember(Member member);


}
