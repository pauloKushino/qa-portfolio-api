package com.paulo.qa.portfolio.service;

import com.paulo.qa.portfolio.dto.TestCaseRequest;
import com.paulo.qa.portfolio.entity.TestCase;
import com.paulo.qa.portfolio.entity.TestSuite;
import com.paulo.qa.portfolio.exception.ResourceNotFoundException;
import com.paulo.qa.portfolio.repository.TestCaseRepository;
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
class TestCaseServiceTest {

    @Mock
    private TestCaseRepository repository;

    @Mock
    private TestSuiteRepository suiteRepository;

    @InjectMocks
    private TestCaseService service;

    private TestSuite suite;
    private TestCase testCase;

    @BeforeEach
    void setUp() {
        suite = TestSuite.builder().id(1L).name("API Tests").type("INTEGRATION").build();
        testCase = TestCase.builder()
                .id(5L)
                .testSuite(suite)
                .name("Should create project")
                .description("Happy path")
                .status("READY")
                .priority("HIGH")
                .tags("api,smoke")
                .build();
    }

    @Test
    void create_shouldUseDefaultsWhenStatusAndPriorityAreMissing() {
        TestCaseRequest request = TestCaseRequest.builder()
                .name("Should create project")
                .description("Happy path")
                .build();
        when(suiteRepository.findById(1L)).thenReturn(Optional.of(suite));
        when(repository.save(any(TestCase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestCase result = service.create(1L, request);

        assertThat(result.getStatus()).isEqualTo("DRAFT");
        assertThat(result.getPriority()).isEqualTo("MEDIUM");
        assertThat(result.getTestSuite()).isSameAs(suite);
        verify(repository).save(any(TestCase.class));
    }

    @Test
    void create_shouldUseProvidedStatusAndPriority() {
        TestCaseRequest request = TestCaseRequest.builder()
                .name("Should create project")
                .status("AUTOMATED")
                .priority("CRITICAL")
                .tags("api")
                .build();
        when(suiteRepository.findById(1L)).thenReturn(Optional.of(suite));
        when(repository.save(any(TestCase.class))).thenReturn(testCase);

        TestCase result = service.create(1L, request);

        assertThat(result).isSameAs(testCase);
        verify(repository).save(any(TestCase.class));
    }

    @Test
    void create_shouldThrowWhenSuiteDoesNotExist() {
        TestCaseRequest request = TestCaseRequest.builder().name("Orphan").build();
        when(suiteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestSuite")
                .hasMessageContaining("99");

        verify(repository, never()).save(any(TestCase.class));
    }

    @Test
    void findBySuiteId_shouldReturnTestCases() {
        when(repository.findByTestSuiteId(1L)).thenReturn(List.of(testCase));

        List<TestCase> result = service.findBySuiteId(1L);

        assertThat(result).containsExactly(testCase);
        verify(repository).findByTestSuiteId(1L);
    }

    @Test
    void findById_shouldReturnTestCaseWhenExists() {
        when(repository.findById(5L)).thenReturn(Optional.of(testCase));

        TestCase result = service.findById(5L);

        assertThat(result).isSameAs(testCase);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestCase")
                .hasMessageContaining("99");
    }

    @Test
    void update_shouldKeepStatusAndPriorityWhenTheyAreNull() {
        when(repository.findById(5L)).thenReturn(Optional.of(testCase));
        when(repository.save(any(TestCase.class))).thenReturn(testCase);
        TestCaseRequest request = TestCaseRequest.builder()
                .name("Updated name")
                .description("Updated description")
                .tags("regression")
                .build();

        TestCase result = service.update(5L, request);

        assertThat(result.getName()).isEqualTo("Updated name");
        assertThat(result.getDescription()).isEqualTo("Updated description");
        assertThat(result.getTags()).isEqualTo("regression");
        assertThat(result.getStatus()).isEqualTo("READY");
        assertThat(result.getPriority()).isEqualTo("HIGH");
    }

    @Test
    void update_shouldApplyStatusAndPriorityWhenProvided() {
        when(repository.findById(5L)).thenReturn(Optional.of(testCase));
        when(repository.save(any(TestCase.class))).thenReturn(testCase);
        TestCaseRequest request = TestCaseRequest.builder()
                .name("Updated name")
                .status("DEPRECATED")
                .priority("LOW")
                .build();

        TestCase result = service.update(5L, request);

        assertThat(result.getStatus()).isEqualTo("DEPRECATED");
        assertThat(result.getPriority()).isEqualTo("LOW");
    }

    @Test
    void delete_shouldDeleteWhenExists() {
        when(repository.existsById(5L)).thenReturn(true);

        service.delete(5L);

        verify(repository).deleteById(5L);
    }

    @Test
    void delete_shouldThrowWhenNotExists() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("TestCase")
                .hasMessageContaining("99");

        verify(repository, never()).deleteById(anyLong());
    }
}
