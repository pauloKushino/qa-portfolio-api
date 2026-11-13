package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.TestSuiteRequest;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import com.paulo.qa.portfolio.repository.TestSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TestSuiteService {

    private final TestSuiteRepository repository;
    private final ProjectRepository projectRepository;

    public TestSuite create(Long projectId, TestSuiteRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        TestSuite suite = TestSuite.builder()
                .project(project)
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .build();
        return repository.save(suite);
    }

    @Transactional(readOnly = true)
    public List<TestSuite> findByProjectId(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public TestSuite findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", id));
    }

    public TestSuite update(Long id, TestSuiteRequest request) {
        TestSuite suite = findById(id);
        suite.setName(request.getName());
        suite.setDescription(request.getDescription());
        suite.setType(request.getType());
        return repository.save(suite);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TestSuite", "id", id);
        }
        repository.deleteById(id);
    }
}