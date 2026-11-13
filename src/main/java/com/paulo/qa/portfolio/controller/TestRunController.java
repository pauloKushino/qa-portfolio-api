package com.paulo.qa.portfolio.controller;

import com.paulo.qa.portfolio.dto.TestRunRequest;
import com.paulo.qa.portfolio.entity.TestRun;
import com.paulo.qa.portfolio.service.TestRunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-suites/{suiteId}/test-runs")
@RequiredArgsConstructor
@Tag(name = "Test Runs", description = "Test run execution")
public class TestRunController {

    private final TestRunService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create test run")
    public TestRun create(@PathVariable Long suiteId, @Valid @RequestBody TestRunRequest request) {
        return service.create(suiteId, request);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start test run")
    public TestRun start(@PathVariable Long id) {
        return service.start(id);
    }

    @PostMapping("/{id}/finish")
    @Operation(summary = "Finish test run with results")
    public TestRun finish(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam int total,
            @RequestParam int passed,
            @RequestParam int failed,
            @RequestParam int skipped
    ) {
        return service.finish(id, status, total, passed, failed, skipped);
    }

    @GetMapping
    @Operation(summary = "List test runs for suite")
    public List<TestRun> findBySuite(@PathVariable Long suiteId) {
        return service.findBySuiteId(suiteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get test run by ID")
    public TestRun findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete test run")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}