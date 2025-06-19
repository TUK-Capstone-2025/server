package com.springboot.tukserver.team.repository;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.team.domain.TeamApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamApplicationHistoryRepository extends JpaRepository<TeamApplicationHistory, Long> {

    List<TeamApplicationHistory> findByMember(Member member);
    List<TeamApplicationHistory> findByMember_UserIdOrderByCreatedAtDesc(String userId);

}
