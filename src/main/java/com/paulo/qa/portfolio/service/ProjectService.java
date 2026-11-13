package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.ProjectRequest;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository repository;

    public Project create(ProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .repositoryUrl(request.getRepositoryUrl())
                .demoUrl(request.getDemoUrl())
                .techStack(request.getTechStack())
                .build();
        return repository.save(project);
    }

    @Transactional(readOnly = true)
    public List<Project> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Project findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", id));
    }

    public Project update(Long id, ProjectRequest request) {
        Project project = findById(id);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setRepositoryUrl(request.getRepositoryUrl());
        project.setDemoUrl(request.getDemoUrl());
        project.setTechStack(request.getTechStack());
        return repository.save(project);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Project", "id", id);
        }
        repository.deleteById(id);
    }
}