package com.paulo.qa.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank
    @Size(max = 255)
    private String name;

    private String description;

    @Size(max = 500)
    private String repositoryUrl;

    @Size(max = 500)
    private String demoUrl;

    private String techStack;
}