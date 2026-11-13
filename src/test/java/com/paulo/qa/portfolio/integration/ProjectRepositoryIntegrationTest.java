package com.paulo.qa.portfolio.integration;

import com.paulo.qa.portfolio.QaPortfolioApiApplication;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = QaPortfolioApiApplication.class)
@Testcontainers
class ProjectRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("qa_portfolio")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProjectRepository repository;

    @Test
    void shouldSaveAndFindProject() {
        Project project = Project.builder()
                .name("Integration Test Project")
                .description("Test Description")
                .repositoryUrl("https://github.com/test")
                .demoUrl("https://demo.com")
                .techStack("Java, Spring Boot, PostgreSQL")
                .build();

        Project saved = repository.save(project);
        assertThat(saved.getId()).isNotNull();

        Project found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Integration Test Project");
        assertThat(found.getTechStack()).isEqualTo("Java, Spring Boot, PostgreSQL");
    }

    @Test
    void shouldUpdateProject() {
        Project project = Project.builder()
                .name("Original")
                .build();
        Project saved = repository.save(project);

        saved.setName("Updated");
        repository.save(saved);

        Project found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Updated");
    }

    @Test
    void shouldDeleteProject() {
        Project project = Project.builder()
                .name("To Delete")
                .build();
        Project saved = repository.save(project);

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }
}