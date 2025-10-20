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
 * Unit tests for the ProductDto class.
 * Tests validation constraints and data handling.
 */
class ProductDtoTest {

    private Validator validator;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        productDto = new ProductDto();
    }

    @Test
    void testValidProductDto() {
        productDto.setName("Valid Product");
        productDto.setPrice(100.0);
        productDto.setBrand("Valid Brand");
        productDto.setSupplierId(1);
        productDto.setDescription("Valid Description");
        productDto.setUrlImage("http://example.com/image.jpg");
        productDto.setStockMinimo(5);
        productDto.setStockActual(10);

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(productDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankNameValidation() {
        productDto.setName("");
        productDto.setPrice(100.0);
        productDto.setBrand("Brand");
        productDto.setSupplierId(1);
        productDto.setUrlImage("http://example.com/image.jpg");

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(productDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNegativePriceValidation() {
        productDto.setName("Product");
        productDto.setPrice(-10.0);
        productDto.setBrand("Brand");
        productDto.setSupplierId(1);
        productDto.setUrlImage("http://example.com/image.jpg");

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(productDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testBlankBrandValidation() {
        productDto.setName("Product");
        productDto.setPrice(100.0);
        productDto.setBrand("");
        productDto.setSupplierId(1);
        productDto.setUrlImage("http://example.com/image.jpg");

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(productDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullSupplierIdValidation() {
        productDto.setName("Product");
        productDto.setPrice(100.0);
        productDto.setBrand("Brand");
        productDto.setSupplierId((Integer) null);
        productDto.setUrlImage("http://example.com/image.jpg");

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(productDto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testFullConstructor() {
        ProductDto dto = new ProductDto(
            "Test Product",
            99.99,
            "Test Brand",
            1,
            "Test Description",
            "http://test.com/image.jpg",
            5,
            10
        );

        assertEquals("Test Product", dto.getName());
        assertEquals(99.99, dto.getPrice());
        assertEquals("Test Brand", dto.getBrand());
        assertEquals(1, dto.getSupplierId());
        assertEquals("Test Description", dto.getDescription());
        assertEquals("http://test.com/image.jpg", dto.getUrlImage());
        assertEquals(5, dto.getStockMinimo());
        assertEquals(10, dto.getStockActual());
    }
}

