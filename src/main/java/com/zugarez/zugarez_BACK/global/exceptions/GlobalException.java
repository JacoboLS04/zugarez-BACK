package com.zugarez.zugarez_BACK.global.exceptions;

import com.zugarez.zugarez_BACK.global.dto.MessageDto;
import com.zugarez.zugarez_BACK.global.utils.Operations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for REST controllers.
 * Maps custom and common exceptions to appropriate HTTP responses with message bodies.
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * Handles ResourceNotFoundException and returns a 404 response.
     * @param e the exception
     * @return MessageDto with NOT_FOUND status and message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageDto> throwNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageDto(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    /**
     * Handles AttributeException and returns a 400 response.
     * @param e the exception
     * @return MessageDto with BAD_REQUEST status and message
     */
    @ExceptionHandler(AttributeException.class)
    public ResponseEntity<MessageDto> throwAttributeException(AttributeException e) {
        return ResponseEntity.badRequest()
                .body(new MessageDto(HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    /**
     * Handles generic Exception and returns a 500 response.
     * @param e the exception
     * @return MessageDto with INTERNAL_SERVER_ERROR status and message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageDto> generalException(Exception e) {
        return ResponseEntity.internalServerError()
                .body(new MessageDto(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    /**
     * Handles validation errors and returns a 400 response with all validation messages.
     * @param e the exception
     * @return MessageDto with BAD_REQUEST status and validation messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDto> validationException(MethodArgumentNotValidException e) {
        List<String> messages = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            messages.add(error.getDefaultMessage());
        });
        return ResponseEntity.badRequest()
                .body(new MessageDto(HttpStatus.BAD_REQUEST, Operations.trimBrackets(messages.toString())));
    }

    /**
     * Handles authentication errors and returns a 404 response.
     * @param e the exception
     * @return MessageDto with NOT_FOUND status and message
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageDto> badCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageDto(HttpStatus.NOT_FOUND, "Credenciales incorrectas"));
    }

    /**
     * Handles access denial errors and returns a 403 response.
     * @param accessDeniedException the exception
     * @return MessageDto with FORBIDDEN status and message
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageDto> accessDeniedException(AccessDeniedException accessDeniedException) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new MessageDto(HttpStatus.FORBIDDEN, "No tienes permiso para acceder a este recurso"));
    }

}
