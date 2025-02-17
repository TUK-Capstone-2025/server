package com.springboot.tukserver.team.repository;

import com.springboot.tukserver.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
