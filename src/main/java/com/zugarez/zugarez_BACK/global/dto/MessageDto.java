package com.zugarez.zugarez_BACK.global.dto;

import org.springframework.http.HttpStatus;

/**
 * Data Transfer Object for sending messages with HTTP status.
 * Used for API responses to provide status and message content.
 */
public class MessageDto {
    /** HTTP status of the response */
    private HttpStatus status;
    /** Message content */
    private String message;

    /**
     * Constructs a MessageDto with status and message.
     * @param status HTTP status
     * @param message Message content
     */
    public MessageDto(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Gets the HTTP status.
     * @return HTTP status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * Sets the HTTP status.
     * @param status HTTP status
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    /**
     * Gets the message content.
     * @return Message content
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content.
     * @param message Message content
     */
    public void setMessage(String message) {
        this.message = message;
    }


}
