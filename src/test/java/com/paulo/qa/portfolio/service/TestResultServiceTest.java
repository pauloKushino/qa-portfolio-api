package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.entity.TestCase;
import com.paulo.qa.portfolio.entity.TestResult;
import com.paulo.qa.portfolio.entity.TestRun;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.TestCaseRepository;
import com.paulo.qa.portfolio.repository.TestResultRepository;
import com.paulo.qa.portfolio.repository.TestRunRepository;
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
class TestResultServiceTest {

    @Mock
    private TestResultRepository repository;

    @Mock
    private TestRunRepository runRepository;

    @Mock
    private TestCaseRepository caseRepository;

    @InjectMocks
    private TestResultService service;

    private TestRun run;
    private TestCase testCase;

    @BeforeEach
    void setUp() {
        run = TestRun.builder().id(1L).name("CI run").build();
        testCase = TestCase.builder().id(2L).name("Should create project").build();
    }

    @Test
    void recordResult_shouldSaveAndReturnResult() {
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));
        when(caseRepository.findById(2L)).thenReturn(Optional.of(testCase));
        when(repository.save(any(TestResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestResult result = service.recordResult(1L, 2L, "PASSED", 150L, null, null);

        assertThat(result.getTestRun()).isSameAs(run);
        assertThat(result.getTestCase()).isSameAs(testCase);
        assertThat(result.getStatus()).isEqualTo("PASSED");
        assertThat(result.getDurationMs()).isEqualTo(150L);
        verify(repository).save(any(TestResult.class));
    }

    @Test
    void recordResult_shouldPersistFailureDetails() {
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));
        when(caseRepository.findById(2L)).thenReturn(Optional.of(testCase));
        when(repository.save(any(TestResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestResult result = service.recordResult(1L, 2L, "FAILED", 500L, "Expected 201 but got 500", "stack trace");

        assertThat(result.getStatus()).isEqualTo("FAILED");
        assertThat(result.getErrorMessage()).isEqualTo("Expected 201 but got 500");
        assertThat(result.getStackTrace()).isEqualTo("stack trace");
    }

    @Test
    void recordResult_shouldThrowWhenRunDoesNotExist() {
        when(runRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recordResult(99L, 2L, "PASSED", 10L, null, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestRun")
                .hasMessageContaining("99");

        verify(repository, never()).save(any(TestResult.class));
    }

    @Test
    void recordResult_shouldThrowWhenTestCaseDoesNotExist() {
        when(runRepository.findById(1L)).thenReturn(Optional.of(run));
        when(caseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recordResult(1L, 99L, "PASSED", 10L, null, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestCase")
                .hasMessageContaining("99");

        verify(repository, never()).save(any(TestResult.class));
    }

    @Test
    void findByRunId_shouldReturnResults() {
        TestResult result = TestResult.builder().id(3L).testRun(run).testCase(testCase).status("PASSED").build();
        when(repository.findByTestRunId(1L)).thenReturn(List.of(result));

        List<TestResult> results = service.findByRunId(1L);

        assertThat(results).containsExactly(result);
        verify(repository).findByTestRunId(1L);
    }
}
