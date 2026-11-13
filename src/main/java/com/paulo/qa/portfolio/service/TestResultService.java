package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.entity.TestResult;
import com.paulo.qa.portfolio.entity.TestRun;
import com.paulo.qa.portfolio.entity.TestCase;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.TestResultRepository;
import com.paulo.qa.portfolio.repository.TestRunRepository;
import com.paulo.qa.portfolio.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestResultService {

    private final TestResultRepository repository;
    private final TestRunRepository runRepository;
    private final TestCaseRepository caseRepository;

    public TestResult recordResult(Long runId, Long caseId, String status, Long durationMs, String errorMessage, String stackTrace) {
        TestRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("TestRun", "id", runId));
        TestCase testCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", caseId));

        TestResult result = TestResult.builder()
                .testRun(run)
                .testCase(testCase)
                .status(status)
                .durationMs(durationMs)
                .errorMessage(errorMessage)
                .stackTrace(stackTrace)
                .build();
        return repository.save(result);
    }

    @Transactional(readOnly = true)
    public List<TestResult> findByRunId(Long runId) {
        return repository.findByTestRunId(runId);
    }
}