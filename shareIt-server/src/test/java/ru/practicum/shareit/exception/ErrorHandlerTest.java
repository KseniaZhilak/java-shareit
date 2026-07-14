package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    private void dummy(String name) {
    }

    private MethodParameter methodParameter() throws NoSuchMethodException {
        return new MethodParameter(ErrorHandlerTest.class.getDeclaredMethod("dummy", String.class), 0);
    }

    @Test
    void handleValidation_collectsFieldErrorsIntoMessage() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "userDto");
        bindingResult.addError(new FieldError("userDto", "email", "must not be blank"));
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter(), bindingResult);

        ErrorHandler.ErrorResponse response = errorHandler.handleValidation(exception);

        assertEquals("email: must not be blank", response.error());
    }

    @Test
    void handleMissingHeader_returnsHeaderNameInMessage() throws NoSuchMethodException {
        MissingRequestHeaderException exception =
                new MissingRequestHeaderException("X-Sharer-User-Id", methodParameter());

        ErrorHandler.ErrorResponse response = errorHandler.handleMissingHeader(exception);

        assertTrue(response.error().contains("X-Sharer-User-Id"));
    }

    @Test
    void handleBadRequest_returnsExceptionMessage() {
        ErrorHandler.ErrorResponse response =
                errorHandler.handleBadRequest(new BadRequestException("Item is not available"));

        assertEquals("Item is not available", response.error());
    }

    @Test
    void handleNotFound_returnsExceptionMessage() {
        ErrorHandler.ErrorResponse response =
                errorHandler.handleNotFound(new NotFoundException("User not found"));

        assertEquals("User not found", response.error());
    }

    @Test
    void handleConflict_returnsExceptionMessage() {
        ErrorHandler.ErrorResponse response =
                errorHandler.handleConflict(new ConflictException("Email already exists"));

        assertEquals("Email already exists", response.error());
    }

    @Test
    void handleForbidden_returnsExceptionMessage() {
        ErrorHandler.ErrorResponse response =
                errorHandler.handleForbidden(new ForbiddenException("Access denied"));

        assertEquals("Access denied", response.error());
    }

}
