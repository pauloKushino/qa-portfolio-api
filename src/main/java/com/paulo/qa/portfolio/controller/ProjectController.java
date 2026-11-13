package com.paulo.qa.portfolio.controller;

import com.paulo.qa.portfolio.dto.ProjectRequest;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "QA Portfolio Projects")
public class ProjectController {

    private final ProjectService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project")
    public Project create(@Valid @RequestBody ProjectRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "List all projects")
    public List<Project> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public Project findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project")
    public Project update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete project")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}