package com.paulo.qa.portfolio.repository;

import com.paulo.qa.portfolio.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByTestRunId(Long testRunId);
}