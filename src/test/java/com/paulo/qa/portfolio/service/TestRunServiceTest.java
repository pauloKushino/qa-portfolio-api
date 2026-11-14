package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.TestRunRequest;
import com.paulo.qa.portfolio.entity.TestRun;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.TestRunRepository;
import com.paulo.qa.portfolio.repository.TestSuiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestRunServiceTest {

    @Mock
    private TestRunRepository repository;

    @Mock
    private TestSuiteRepository suiteRepository;

    @InjectMocks
    private TestRunService service;

    private TestSuite suite;
    private TestRun run;

    @BeforeEach
    void setUp() {
        suite = TestSuite.builder().id(1L).name("API Tests").type("INTEGRATION").build();
        run = TestRun.builder()
                .id(7L)
                .testSuite(suite)
                .name("CI run")
                .status("PENDING")
                .environment("CI")
                .ciBuildUrl("https://github.com/pauloKushino/qa-portfolio-api/actions")
                .build();
    }

    @Test
    void create_shouldUseDefaultsWhenStatusIsMissing() {
        TestRunRequest request = TestRunRequest.builder()
                .name("CI run")
                .environment("CI")
                .ciBuildUrl("https://github.com/pauloKushino/qa-portfolio-api/actions")
                .build();
        when(suiteRepository.findById(1L)).thenReturn(Optional.of(suite));
        when(repository.save(any(TestRun.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestRun result = service.create(1L, request);

        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getTestSuite()).isSameAs(suite);
        assertThat(result.getEnvironment()).isEqualTo("CI");
        verify(repository).save(any(TestRun.class));
    }

    @Test
    void create_shouldUseProvidedStatus() {
        TestRunRequest request = TestRunRequest.builder()
                .name("Nightly")
                .status("RUNNING")
                .build();
        when(suiteRepository.findById(1L)).thenReturn(Optional.of(suite));
        when(repository.save(any(TestRun.class))).thenReturn(run);

        TestRun result = service.create(1L, request);

        assertThat(result).isSameAs(run);
    }

    @Test
    void create_shouldThrowWhenSuiteDoesNotExist() {
        TestRunRequest request = TestRunRequest.builder().name("Orphan").build();
        when(suiteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestSuite")
                .hasMessageContaining("99");

        verify(repository, never()).save(any(TestRun.class));
    }

    @Test
    void start_shouldSetStatusAndStartedAt() {
        when(repository.findById(7L)).thenReturn(Optional.of(run));
        when(repository.save(run)).thenReturn(run);

        LocalDateTime before = LocalDateTime.now();
        TestRun result = service.start(7L);

        assertThat(result.getStatus()).isEqualTo("RUNNING");
        assertThat(result.getStartedAt()).isNotNull().isAfterOrEqualTo(before);
        verify(repository).save(run);
    }

    @Test
    void finish_shouldPersistSummary() {
        when(repository.findById(7L)).thenReturn(Optional.of(run));
        when(repository.save(run)).thenReturn(run);

        TestRun result = service.finish(7L, "FAILED", 10, 8, 2, 0);

        assertThat(result.getStatus()).isEqualTo("FAILED");
        assertThat(result.getFinishedAt()).isNotNull();
        assertThat(result.getTotalTests()).isEqualTo(10);
        assertThat(result.getPassedTests()).isEqualTo(8);
        assertThat(result.getFailedTests()).isEqualTo(2);
        assertThat(result.getSkippedTests()).isZero();
    }

    @Test
    void findBySuiteId_shouldReturnRuns() {
        when(repository.findByTestSuiteId(1L)).thenReturn(List.of(run));

        List<TestRun> result = service.findBySuiteId(1L);

        assertThat(result).containsExactly(run);
        verify(repository).findByTestSuiteId(1L);
    }

    @Test
    void findById_shouldReturnRunWhenExists() {
        when(repository.findById(7L)).thenReturn(Optional.of(run));

        TestRun result = service.findById(7L);

        assertThat(result).isSameAs(run);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestRun")
                .hasMessageContaining("99");
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(repository.existsById(7L)).thenReturn(true);

        service.delete(7L);

        verify(repository).deleteById(7L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestRun")
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(anyLong());
    }
}
