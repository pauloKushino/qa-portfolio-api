package com.paulo.qa.portfolio.repository;

import com.paulo.qa.portfolio.entity.Bug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findByProjectId(Long projectId);
    List<Bug> findByStatus(String status);
}