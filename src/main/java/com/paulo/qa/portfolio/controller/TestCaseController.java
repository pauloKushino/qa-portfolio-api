package com.paulo.qa.portfolio.controller;

import com.paulo.qa.portfolio.dto.TestCaseRequest;
import com.paulo.qa.portfolio.entity.TestCase;
import com.paulo.qa.portfolio.service.TestCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-suites/{suiteId}/test-cases")
@RequiredArgsConstructor
@Tag(name = "Test Cases", description = "Test case management")
public class TestCaseController {

    private final TestCaseService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create test case")
    public TestCase create(@PathVariable Long suiteId, @Valid @RequestBody TestCaseRequest request) {
        return service.create(suiteId, request);
    }

    @GetMapping
    @Operation(summary = "List test cases for suite")
    public List<TestCase> findBySuite(@PathVariable Long suiteId) {
        return service.findBySuiteId(suiteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get test case by ID")
    public TestCase findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update test case")
    public TestCase update(@PathVariable Long id, @Valid @RequestBody TestCaseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete test case")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}