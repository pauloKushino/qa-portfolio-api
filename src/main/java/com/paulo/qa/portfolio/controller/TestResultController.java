package com.paulo.qa.portfolio.controller;

import com.paulo.qa.portfolio.entity.TestResult;
import com.paulo.qa.portfolio.service.TestResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-runs/{runId}/results")
@RequiredArgsConstructor
@Tag(name = "Test Results", description = "Individual test results")
public class TestResultController {

    private final TestResultService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record test result")
    public TestResult record(
            @PathVariable Long runId,
            @RequestParam Long caseId,
            @RequestParam String status,
            @RequestParam(required = false) Long durationMs,
            @RequestParam(required = false) String errorMessage,
            @RequestParam(required = false) String stackTrace
    ) {
        return service.recordResult(runId, caseId, status, durationMs, errorMessage, stackTrace);
    }

    @GetMapping
    @Operation(summary = "List results for test run")
    public List<TestResult> findByRun(@PathVariable Long runId) {
        return service.findByRunId(runId);
    }
}