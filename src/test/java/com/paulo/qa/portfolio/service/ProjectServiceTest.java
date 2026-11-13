package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.ProjectRequest;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository repository;

    @InjectMocks
    private ProjectService service;

    private Project project;
    private ProjectRequest request;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(1L)
                .name("Test Project")
                .description("Description")
                .repositoryUrl("https://github.com/test")
                .demoUrl("https://demo.com")
                .techStack("Java, Spring")
                .build();

        request = ProjectRequest.builder()
                .name("Test Project")
                .description("Description")
                .repositoryUrl("https://github.com/test")
                .demoUrl("https://demo.com")
                .techStack("Java, Spring")
                .build();
    }

    @Test
    void create_shouldSaveAndReturnProject() {
        when(repository.save(any(Project.class))).thenReturn(project);

        Project result = service.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Project");
        verify(repository).save(any(Project.class));
    }

    @Test
    void findAll_shouldReturnAllProjects() {
        when(repository.findAll()).thenReturn(List.of(project));

        List<Project> result = service.findAll();

        assertThat(result).hasSize(1);
        verify(repository).findAll();
    }

    @Test
    void findById_shouldReturnProjectWhenExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(project));

        Project result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repository).findById(1L);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found with id: 99");

        verify(repository).findById(99L);
    }

    @Test
    void update_shouldUpdateAndReturnProject() {
        when(repository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(any(Project.class))).thenReturn(project);

        ProjectRequest updateRequest = ProjectRequest.builder()
                .name("Updated Project")
                .description("New Description")
                .build();

        Project result = service.update(1L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Project");
        verify(repository).findById(1L);
        verify(repository).save(any(Project.class));
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found with id: 99");

        verify(repository).existsById(99L);
        verify(repository, never()).deleteById(anyLong());
    }
}