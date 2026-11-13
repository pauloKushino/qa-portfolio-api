package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.TestCaseRequest;
import com.paulo.qa.portfolio.entity.TestCase;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.TestCaseRepository;
import com.paulo.qa.portfolio.repository.TestSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestCaseService {

    private final TestCaseRepository repository;
    private final TestSuiteRepository suiteRepository;

    public TestCase create(Long suiteId, TestCaseRequest request) {
        TestSuite suite = suiteRepository.findById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", suiteId));

        TestCase testCase = TestCase.builder()
                .testSuite(suite)
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : "DRAFT")
                .priority(request.getPriority() != null ? request.getPriority() : "MEDIUM")
                .tags(request.getTags())
                .build();
        return repository.save(testCase);
    }

    @Transactional(readOnly = true)
    public List<TestCase> findBySuiteId(Long suiteId) {
        return repository.findByTestSuiteId(suiteId);
    }

    @Transactional(readOnly = true)
    public TestCase findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));
    }

    public TestCase update(Long id, TestCaseRequest request) {
        TestCase testCase = findById(id);
        testCase.setName(request.getName());
        testCase.setDescription(request.getDescription());
        if (request.getStatus() != null) testCase.setStatus(request.getStatus());
        if (request.getPriority() != null) testCase.setPriority(request.getPriority());
        testCase.setTags(request.getTags());
        return repository.save(testCase);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }
        repository.deleteById(id);
    }
}