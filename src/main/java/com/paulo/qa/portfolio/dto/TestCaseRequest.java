package com.paulo.qa.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @Size(max = 50)
    private String status; // DRAFT, READY, AUTOMATED, DEPRECATED

    @Size(max = 20)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    private String tags;
}