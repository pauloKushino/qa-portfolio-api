package com.paulo.qa.portfolio.repository;

import com.paulo.qa.portfolio.entity.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {
    List<TestRun> findByTestSuiteId(Long testSuiteId);
}