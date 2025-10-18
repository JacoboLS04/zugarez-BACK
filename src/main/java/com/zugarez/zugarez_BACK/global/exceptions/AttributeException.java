package com.zugarez.zugarez_BACK.global.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an attribute validation or business rule fails.
 * Results in a 400 BAD REQUEST HTTP response.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AttributeException extends Exception {
    /**
     * Constructs a new AttributeException with the specified detail message.
     * @param message the detail message
     */
    public AttributeException(String message) {
        super(message);
    }

    /**
     * Constructs a new AttributeException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public AttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
