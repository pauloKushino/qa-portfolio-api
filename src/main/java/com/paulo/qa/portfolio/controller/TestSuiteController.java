package com.paulo.qa.portfolio.controller;

import com.paulo.qa.portfolio.dto.TestSuiteRequest;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.service.TestSuiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/test-suites")
@RequiredArgsConstructor
@Tag(name = "Test Suites", description = "Test suite management")
public class TestSuiteController {

    private final TestSuiteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create test suite for project")
    public TestSuite create(@PathVariable Long projectId, @Valid @RequestBody TestSuiteRequest request) {
        return service.create(projectId, request);
    }

    @GetMapping
    @Operation(summary = "List test suites for project")
    public List<TestSuite> findByProject(@PathVariable Long projectId) {
        return service.findByProjectId(projectId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get test suite by ID")
    public TestSuite findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update test suite")
    public TestSuite update(@PathVariable Long id, @Valid @RequestBody TestSuiteRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete test suite")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}