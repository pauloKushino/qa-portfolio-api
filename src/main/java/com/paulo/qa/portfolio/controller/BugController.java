package com.paulo.qa.portfolio.controller;

import com.paulo.qa.portfolio.dto.BugRequest;
import com.paulo.qa.portfolio.entity.Bug;
import com.paulo.qa.portfolio.service.BugService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/bugs")
@RequiredArgsConstructor
@Tag(name = "Bugs", description = "Bug tracking")
public class BugController {

    private final BugService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Report a bug")
    public Bug create(@PathVariable Long projectId, @Valid @RequestBody BugRequest request) {
        return service.create(projectId, request);
    }

    @GetMapping
    @Operation(summary = "List bugs for project")
    public List<Bug> findByProject(@PathVariable Long projectId) {
        return service.findByProjectId(projectId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bug by ID")
    public Bug findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update bug")
    public Bug update(@PathVariable Long id, @Valid @RequestBody BugRequest request) {
        return service.update(id, request);
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Mark bug as resolved")
    public Bug resolve(@PathVariable Long id) {
        return service.resolve(id);
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close bug")
    public Bug close(@PathVariable Long id) {
        return service.close(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete bug")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}