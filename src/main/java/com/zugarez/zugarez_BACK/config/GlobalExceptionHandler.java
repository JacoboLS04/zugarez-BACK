package com.zugarez.zugarez_BACK.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler { // <-- nombre cambiado

    private Map<String, Object> body(String code, String message, Object details, String path) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", OffsetDateTime.now().toString());
        m.put("error", code);
        m.put("message", message);
        if (details != null) m.put("details", details);
        if (path != null) m.put("path", path);
        return m;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> fields = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "rejectedValue", String.valueOf(fe.getRejectedValue()),
                        "message", fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage()))
                .collect(Collectors.toList());
        log.warn("Error de validación: {}", fields);
        return ResponseEntity.badRequest().body(body("VALIDATION_ERROR", "Los datos enviados no son válidos", fields, null));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        List<Map<String, String>> violations = ex.getConstraintViolations().stream()
                .map(v -> Map.of(
                        "property", v.getPropertyPath().toString(),
                        "invalidValue", String.valueOf(v.getInvalidValue()),
                        "message", v.getMessage()))
                .collect(Collectors.toList());
        log.warn("Violaciones de constraint: {}", violations);
        return ResponseEntity.badRequest().body(body("CONSTRAINT_VIOLATION", "Parámetros inválidos", violations, null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("JSON inválido o tipos incompatibles: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().body(body("MALFORMED_JSON", "JSON inválido o tipos incompatibles", ex.getMostSpecificCause().getMessage(), null));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        log.warn("Solicitud incorrecta: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(body("BAD_REQUEST", "Solicitud incorrecta", ex.getMessage(), null));
    }

    @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body("NOT_FOUND", "Recurso no encontrado", ex.getMessage(), null));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException ex) {
        log.warn("Ruta no encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body("NOT_FOUND", "Endpoint no existe", ex.getMessage(), null));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String msg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.warn("Violación de integridad de datos: {}", msg);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body("DATA_INTEGRITY_VIOLATION", "Violación de integridad/constraint", msg, null));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleIllegal(RuntimeException ex) {
        log.warn("Error de negocio/argumento: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(body("BUSINESS_ERROR", "Solicitud inválida", ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Error interno no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body("INTERNAL_ERROR", "Error interno del servidor", ex.getMessage(), null));
    }
}
