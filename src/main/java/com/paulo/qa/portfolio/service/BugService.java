package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.BugRequest;
import com.paulo.qa.portfolio.entity.Bug;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.entity.TestCase;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.BugRepository;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import com.paulo.qa.portfolio.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BugService {

    private final BugRepository repository;
    private final ProjectRepository projectRepository;
    private final TestCaseRepository testCaseRepository;

    public Bug create(Long projectId, BugRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        TestCase testCase = null;
        if (request.getTitle() != null && request.getTitle().contains("TC-")) {
            // Optional: try to link to test case if mentioned in title
        }

        Bug bug = Bug.builder()
                .project(project)
                .testCase(testCase)
                .title(request.getTitle())
                .description(request.getDescription())
                .severity(request.getSeverity() != null ? request.getSeverity() : "MEDIUM")
                .status(request.getStatus() != null ? request.getStatus() : "OPEN")
                .reportedBy(request.getReportedBy())
                .assignedTo(request.getAssignedTo())
                .build();
        return repository.save(bug);
    }

    @Transactional(readOnly = true)
    public List<Bug> findByProjectId(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public Bug findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bug", "id", id));
    }

    public Bug update(Long id, BugRequest request) {
        Bug bug = findById(id);
        bug.setTitle(request.getTitle());
        bug.setDescription(request.getDescription());
        if (request.getSeverity() != null) bug.setSeverity(request.getSeverity());
        if (request.getStatus() != null) bug.setStatus(request.getStatus());
        bug.setAssignedTo(request.getAssignedTo());
        return repository.save(bug);
    }

    public Bug resolve(Long id) {
        Bug bug = findById(id);
        bug.setStatus("FIXED");
        bug.setResolvedAt(LocalDateTime.now());
        return repository.save(bug);
    }

    public Bug close(Long id) {
        Bug bug = findById(id);
        bug.setStatus("CLOSED");
        bug.setClosedAt(LocalDateTime.now());
        return repository.save(bug);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Bug", "id", id);
        }
        repository.deleteById(id);
    }
}