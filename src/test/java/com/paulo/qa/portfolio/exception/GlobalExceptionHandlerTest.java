package com.paulo.qa.portfolio.exception;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404WithMessage() {
        GlobalExceptionHandler.ErrorResponse response =
                handler.handleNotFound(new ResourceNotFoundException("Project", "id", 99L));

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.message()).contains("Project").contains("99");
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void handleEntityNotFound_shouldReturn404WithMessage() {
        GlobalExceptionHandler.ErrorResponse response =
                handler.handleEntityNotFound(new EntityNotFoundException("Entity missing"));

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.message()).isEqualTo("Entity missing");
    }

    @Test
    void handleValidation_shouldReturn400WithFieldErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "must not be blank"));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        GlobalExceptionHandler.ValidationErrorResponse response = handler.handleValidation(exception);

        assertThat(response.status()).isEqualTo(400);
        assertThat(response.message()).isEqualTo("Validation failed");
        assertThat(response.errors()).containsEntry("name", "must not be blank");
    }

    @Test
    void handleGeneric_shouldHideInternalDetails() {
        GlobalExceptionHandler.ErrorResponse response =
                handler.handleGeneric(new RuntimeException("database password leaked"));

        assertThat(response.status()).isEqualTo(500);
        assertThat(response.message()).isEqualTo("Internal server error");
        assertThat(response.message()).doesNotContain("password");
    }
}
