package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.TestRunRequest;
import com.paulo.qa.portfolio.entity.TestRun;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.TestRunRepository;
import com.paulo.qa.portfolio.repository.TestSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestRunService {

    private final TestRunRepository repository;
    private final TestSuiteRepository suiteRepository;

    public TestRun create(Long suiteId, TestRunRequest request) {
        TestSuite suite = suiteRepository.findById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", suiteId));

        TestRun run = TestRun.builder()
                .testSuite(suite)
                .name(request.getName())
                .status(request.getStatus() != null ? request.getStatus() : "PENDING")
                .environment(request.getEnvironment())
                .ciBuildUrl(request.getCiBuildUrl())
                .build();
        return repository.save(run);
    }

    public TestRun start(Long id) {
        TestRun run = findById(id);
        run.setStatus("RUNNING");
        run.setStartedAt(LocalDateTime.now());
        return repository.save(run);
    }

    public TestRun finish(Long id, String status, int total, int passed, int failed, int skipped) {
        TestRun run = findById(id);
        run.setStatus(status);
        run.setFinishedAt(LocalDateTime.now());
        run.setTotalTests(total);
        run.setPassedTests(passed);
        run.setFailedTests(failed);
        run.setSkippedTests(skipped);
        return repository.save(run);
    }

    @Transactional(readOnly = true)
    public List<TestRun> findBySuiteId(Long suiteId) {
        return repository.findByTestSuiteId(suiteId);
    }

    @Transactional(readOnly = true)
    public TestRun findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestRun", "id", id));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TestRun", "id", id);
        }
        repository.deleteById(id);
    }
}