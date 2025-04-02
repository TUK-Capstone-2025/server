package com.springboot.tukserver.member.repository;

import com.springboot.tukserver.member.domain.Member;
import com.springboot.tukserver.member.domain.MemberRole;
import com.springboot.tukserver.member.domain.MemberStatus;
import com.springboot.tukserver.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    List<Member> findByRoleNot(MemberRole role);
    boolean existsByUserId(String userId);
    List<Member> findByTeamAndStatus(Team team, MemberStatus status);
    List<Member> findAllByUserId(String userId);

}
