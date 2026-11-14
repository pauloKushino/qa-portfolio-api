package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.TestSuiteRequest;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import com.paulo.qa.portfolio.repository.TestSuiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestSuiteServiceTest {

    @Mock
    private TestSuiteRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TestSuiteService service;

    private Project project;
    private TestSuite suite;
    private TestSuiteRequest request;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(1L).name("QA Portfolio").build();
        suite = TestSuite.builder()
                .id(10L)
                .project(project)
                .name("API Tests")
                .description("Contract and integration tests")
                .type("INTEGRATION")
                .build();
        request = TestSuiteRequest.builder()
                .name("API Tests")
                .description("Contract and integration tests")
                .type("INTEGRATION")
                .build();
    }

    @Test
    void create_shouldSaveAndReturnSuite() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(any(TestSuite.class))).thenReturn(suite);

        TestSuite result = service.create(1L, request);

        assertThat(result).isSameAs(suite);
        verify(projectRepository).findById(1L);
        verify(repository).save(any(TestSuite.class));
    }

    @Test
    void create_shouldThrowWhenProjectDoesNotExist() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project")
                .hasMessageContaining("99");

        verify(repository, never()).save(any(TestSuite.class));
    }

    @Test
    void findByProjectId_shouldReturnSuites() {
        when(repository.findByProjectId(1L)).thenReturn(List.of(suite));

        List<TestSuite> result = service.findByProjectId(1L);

        assertThat(result).containsExactly(suite);
        verify(repository).findByProjectId(1L);
    }

    @Test
    void findById_shouldReturnSuiteWhenExists() {
        when(repository.findById(10L)).thenReturn(Optional.of(suite));

        TestSuite result = service.findById(10L);

        assertThat(result).isSameAs(suite);
        verify(repository).findById(10L);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestSuite")
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldUpdateAndReturnSuite() {
        when(repository.findById(10L)).thenReturn(Optional.of(suite));
        when(repository.save(any(TestSuite.class))).thenReturn(suite);

        TestSuiteRequest updateRequest = TestSuiteRequest.builder()
                .name("Updated Suite")
                .description("Updated description")
                .type("E2E")
                .build();

        TestSuite result = service.update(10L, updateRequest);

        assertThat(result.getName()).isEqualTo("Updated Suite");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getType()).isEqualTo("E2E");
        verify(repository).save(suite);
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(repository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(repository).deleteById(10L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestSuite")
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(anyLong());
    }
}
