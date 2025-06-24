package com.springboot.tukserver.team.repository;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.team.domain.TeamApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamApplicationHistoryRepository extends JpaRepository<TeamApplicationHistory, Long> {

    @Query("""
    SELECT h FROM TeamApplicationHistory h
    WHERE h.member.memberId = :memberId
      AND h.status = 'REJECT'
      AND h.id IN (
          SELECT MAX(h2.id) FROM TeamApplicationHistory h2
          WHERE h2.member.memberId = :memberId
            AND h2.team = h.team
            AND h2.status = 'REJECT'
      )
    """)
    List<TeamApplicationHistory> findLatestRejectsByUserIdGroupedByTeam(@Param("memberId") Long memberId);

}
