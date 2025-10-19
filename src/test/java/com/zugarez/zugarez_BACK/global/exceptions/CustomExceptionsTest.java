package com.zugarez.zugarez_BACK.global.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for custom exceptions.
 * Tests exception creation and messages.
 */
class CustomExceptionsTest {

    @Test
    void resourceNotFoundException_WithMessage_ShouldCreateException() {
        // Arrange
        String message = "Resource not found";

        // Act
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void attributeException_WithMessage_ShouldCreateException() {
        // Arrange
        String message = "Attribute validation failed";

        // Act
        AttributeException exception = new AttributeException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void resourceNotFoundException_ShouldBeThrowable() {
        // Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            throw new ResourceNotFoundException("Test exception");
        });
    }

    @Test
    void attributeException_ShouldBeThrowable() {
        // Assert
        assertThrows(AttributeException.class, () -> {
            throw new AttributeException("Test exception");
        });
    }

    @Test
    void resourceNotFoundException_ShouldBeCatchableAsException() {
        // Act & Assert
        try {
            throw new ResourceNotFoundException("Test message");
        } catch (Exception e) {
            assertTrue(e instanceof ResourceNotFoundException);
            assertEquals("Test message", e.getMessage());
        }
    }

    @Test
    void attributeException_ShouldBeCatchableAsException() {
        // Act & Assert
        try {
            throw new AttributeException("Test message");
        } catch (Exception e) {
            assertTrue(e instanceof AttributeException);
            assertEquals("Test message", e.getMessage());
        }
    }
}

