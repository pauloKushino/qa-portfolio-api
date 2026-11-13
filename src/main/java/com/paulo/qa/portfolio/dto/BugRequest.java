package com.paulo.qa.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    private String description;

    @Size(max = 20)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @Size(max = 50)
    private String status; // OPEN, IN_PROGRESS, FIXED, VERIFIED, CLOSED, WONT_FIX

    @Size(max = 100)
    private String reportedBy;

    @Size(max = 100)
    private String assignedTo;
}