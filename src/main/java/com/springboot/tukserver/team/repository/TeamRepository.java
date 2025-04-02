package com.springboot.tukserver.team.repository;

import com.springboot.tukserver.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByName(String name);
    Optional<Team> findByLeader(String leader);

}
