package com.zugarez.zugarez_BACK.global.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exceptions.
 */
class CustomExceptionsTest {

    @Test
    void testResourceNotFoundException_WithMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void testResourceNotFoundException_WithMessageAndCause() {
        String message = "Resource not found";
        Throwable cause = new RuntimeException("Root cause");
        ResourceNotFoundException exception = new ResourceNotFoundException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testAttributeException_WithMessage() {
        String message = "Invalid attribute";
        AttributeException exception = new AttributeException(message);

        assertEquals(message, exception.getMessage());
        assertNotNull(exception);
    }

    @Test
    void testAttributeException_WithMessageAndCause() {
        String message = "Invalid attribute";
        Throwable cause = new RuntimeException("Root cause");
        AttributeException exception = new AttributeException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testResourceNotFoundException_HasCorrectStatus() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        // Verify the annotation is present
        assertNotNull(exception.getClass().getAnnotation(
            org.springframework.web.bind.annotation.ResponseStatus.class));

        // Verify the status is NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND,
            exception.getClass().getAnnotation(
                org.springframework.web.bind.annotation.ResponseStatus.class).value());
    }

    @Test
    void testAttributeException_HasCorrectStatus() {
        AttributeException exception = new AttributeException("Test");

        // Verify the annotation is present
        assertNotNull(exception.getClass().getAnnotation(
            org.springframework.web.bind.annotation.ResponseStatus.class));

        // Verify the status is BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST,
            exception.getClass().getAnnotation(
                org.springframework.web.bind.annotation.ResponseStatus.class).value());
    }
}

