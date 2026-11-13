package com.paulo.qa.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRunRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 50)
    private String status; // PENDING, RUNNING, PASSED, FAILED, CANCELLED

    @Size(max = 100)
    private String environment; // LOCAL, CI, STAGING, PROD

    private String ciBuildUrl;
}