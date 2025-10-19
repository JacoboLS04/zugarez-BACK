package com.zugarez.zugarez_BACK.CRUD.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validation tests for ProductDto.
 * Tests Jakarta Bean Validation annotations.
 */
class ProductDtoTest {

    private Validator validator;
    private ProductDto validProductDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validProductDto = new ProductDto();
        validProductDto.setName("Valid Product");
        validProductDto.setPrice(100.0);
        validProductDto.setBrand("Valid Brand");
        validProductDto.setSupplierId(1);
        validProductDto.setDescription("Valid Description");
        validProductDto.setUrlImage("http://valid.com/image.jpg");
        validProductDto.setStockMinimo(10);
        validProductDto.setStockActual(50);
    }

    @Test
    void validate_WithValidData_ShouldHaveNoViolations() {
        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_WithBlankName_ShouldHaveViolation() {
        // Arrange
        validProductDto.setName("");

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("nombre") || v.getMessage().contains("vacío")));
    }

    @Test
    void validate_WithNullName_ShouldHaveViolation() {
        // Arrange
        validProductDto.setName(null);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_WithNegativePrice_ShouldHaveViolation() {
        // Arrange
        validProductDto.setPrice(-10.0);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("precio") || v.getMessage().contains("mayor")));
    }

    @Test
    void validate_WithZeroPrice_ShouldHaveNoViolation() {
        // Arrange
        validProductDto.setPrice(0.0);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_WithBlankBrand_ShouldHaveViolation() {
        // Arrange
        validProductDto.setBrand("");

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("marca")));
    }

    @Test
    void validate_WithNullSupplierId_ShouldHaveViolation() {
        // Arrange
        validProductDto.setSupplierId((Integer) null);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("proveedor") || v.getMessage().contains("requerido")));
    }

    @Test
    void validate_WithBlankUrlImage_ShouldHaveViolation() {
        // Arrange
        validProductDto.setUrlImage("");

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("url") || v.getMessage().contains("imagen")));
    }

    @Test
    void validate_WithNegativeStockMinimo_ShouldHaveViolation() {
        // Arrange
        validProductDto.setStockMinimo(-5);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("stock")));
    }

    @Test
    void validate_WithNegativeStockActual_ShouldHaveViolation() {
        // Arrange
        validProductDto.setStockActual(-5);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("stock")));
    }

    @Test
    void validate_WithNullDescription_ShouldHaveNoViolation() {
        // Arrange - Description is optional
        validProductDto.setDescription(null);

        // Act
        Set<ConstraintViolation<ProductDto>> violations = validator.validate(validProductDto);

        // Assert
        assertTrue(violations.isEmpty());
    }
}
