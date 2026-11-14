package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.BugRequest;
import com.paulo.qa.portfolio.entity.Bug;
import com.paulo.qa.portfolio.entity.Project;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.BugRepository;
import com.paulo.qa.portfolio.repository.ProjectRepository;
import com.paulo.qa.portfolio.repository.TestCaseRepository;
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
class BugServiceTest {

    @Mock
    private BugRepository repository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TestCaseRepository testCaseRepository;

    @InjectMocks
    private BugService service;

    private Project project;
    private Bug bug;

    @BeforeEach
    void setUp() {
        project = Project.builder().id(1L).name("QA Portfolio").build();
        bug = Bug.builder()
                .id(10L)
                .project(project)
                .title("Flaky contract test")
                .description("Intermittent failure")
                .severity("HIGH")
                .status("OPEN")
                .reportedBy("qa")
                .assignedTo("dev")
                .build();
    }

    @Test
    void create_shouldUseDefaultsWhenSeverityAndStatusAreMissing() {
        BugRequest request = BugRequest.builder()
                .title("Flaky contract test")
                .description("Intermittent failure")
                .reportedBy("qa")
                .build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(any(Bug.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bug result = service.create(1L, request);

        assertThat(result.getProject()).isSameAs(project);
        assertThat(result.getSeverity()).isEqualTo("MEDIUM");
        assertThat(result.getStatus()).isEqualTo("OPEN");
        assertThat(result.getReportedBy()).isEqualTo("qa");
        verify(repository).save(any(Bug.class));
    }

    @Test
    void create_shouldUseProvidedValuesAndHandleTestCaseMention() {
        BugRequest request = BugRequest.builder()
                .title("TC-42 fails on CI")
                .description("Assertion error")
                .severity("CRITICAL")
                .status("IN_PROGRESS")
                .assignedTo("dev")
                .build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(repository.save(any(Bug.class))).thenReturn(bug);

        Bug result = service.create(1L, request);

        assertThat(result).isSameAs(bug);
        verify(repository).save(any(Bug.class));
        verifyNoInteractions(testCaseRepository);
    }

    @Test
    void create_shouldThrowWhenProjectDoesNotExist() {
        BugRequest request = BugRequest.builder().title("Orphan bug").build();
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project")
                .hasMessageContaining("99");

        verify(repository, never()).save(any(Bug.class));
    }

    @Test
    void findByProjectId_shouldReturnBugs() {
        when(repository.findByProjectId(1L)).thenReturn(List.of(bug));

        List<Bug> result = service.findByProjectId(1L);

        assertThat(result).containsExactly(bug);
        verify(repository).findByProjectId(1L);
    }

    @Test
    void findById_shouldReturnBugWhenExists() {
        when(repository.findById(10L)).thenReturn(Optional.of(bug));

        Bug result = service.findById(10L);

        assertThat(result).isSameAs(bug);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Bug")
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldKeepSeverityAndStatusWhenTheyAreNull() {
        when(repository.findById(10L)).thenReturn(Optional.of(bug));
        when(repository.save(any(Bug.class))).thenReturn(bug);
        BugRequest request = BugRequest.builder()
                .title("Updated title")
                .description("Updated description")
                .assignedTo("another-dev")
                .build();

        Bug result = service.update(10L, request);

        assertThat(result.getTitle()).isEqualTo("Updated title");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getSeverity()).isEqualTo("HIGH");
        assertThat(result.getStatus()).isEqualTo("OPEN");
        assertThat(result.getAssignedTo()).isEqualTo("another-dev");
    }

    @Test
    void update_shouldApplySeverityAndStatusWhenProvided() {
        when(repository.findById(10L)).thenReturn(Optional.of(bug));
        when(repository.save(any(Bug.class))).thenReturn(bug);
        BugRequest request = BugRequest.builder()
                .title("Updated title")
                .severity("LOW")
                .status("VERIFIED")
                .build();

        Bug result = service.update(10L, request);

        assertThat(result.getSeverity()).isEqualTo("LOW");
        assertThat(result.getStatus()).isEqualTo("VERIFIED");
    }

    @Test
    void resolve_shouldSetStatusAndResolvedAt() {
        when(repository.findById(10L)).thenReturn(Optional.of(bug));
        when(repository.save(bug)).thenReturn(bug);

        Bug result = service.resolve(10L);

        assertThat(result.getStatus()).isEqualTo("FIXED");
        assertThat(result.getResolvedAt()).isNotNull();
    }

    @Test
    void close_shouldSetStatusAndClosedAt() {
        when(repository.findById(10L)).thenReturn(Optional.of(bug));
        when(repository.save(bug)).thenReturn(bug);

        Bug result = service.close(10L);

        assertThat(result.getStatus()).isEqualTo("CLOSED");
        assertThat(result.getClosedAt()).isNotNull();
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
                .hasMessageContaining("Bug")
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(anyLong());
    }
}
