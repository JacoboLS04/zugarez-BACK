package com.zugarez.zugarez_BACK.CRUD.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product entity.
 * Tests entity properties, constructors, and business logic.
 */
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    void constructor_WithNoArgs_ShouldCreateEmptyProduct() {
        // Assert
        assertNotNull(product);
        assertEquals(0, product.getId());
        assertNull(product.getName());
    }

    @Test
    void constructor_WithAllArgs_ShouldCreateProductWithValues() {
        // Act
        Product fullProduct = new Product(
                1, "Test Product", 100.0, "Test Brand",
                1, "Description", "http://test.com/img.jpg", 10, 50
        );

        // Assert
        assertEquals(1, fullProduct.getId());
        assertEquals("Test Product", fullProduct.getName());
        assertEquals(100.0, fullProduct.getPrice());
        assertEquals("Test Brand", fullProduct.getBrand());
        assertEquals(1, fullProduct.getSupplierId());
        assertEquals("Description", fullProduct.getDescription());
        assertEquals("http://test.com/img.jpg", fullProduct.getUrlImage());
        assertEquals(10, fullProduct.getStockMinimo());
        assertEquals(50, fullProduct.getStockActual());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Act
        product.setId(1);
        product.setName("Test Product");
        product.setPrice(150.0);
        product.setBrand("Brand X");
        product.setSupplierId(2);
        product.setDescription("Test Description");
        product.setUrlImage("http://example.com/image.jpg");
        product.setStockMinimo(5);
        product.setStockActual(30);

        // Assert
        assertEquals(1, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(150.0, product.getPrice());
        assertEquals("Brand X", product.getBrand());
        assertEquals(2, product.getSupplierId());
        assertEquals("Test Description", product.getDescription());
        assertEquals("http://example.com/image.jpg", product.getUrlImage());
        assertEquals(5, product.getStockMinimo());
        assertEquals(30, product.getStockActual());
    }

    @Test
    void setPrice_WithNegativeValue_ShouldSetValue() {
        // Act
        product.setPrice(-50.0);

        // Assert - Entity allows it, validation is in DTO/Service layer
        assertEquals(-50.0, product.getPrice());
    }

    @Test
    void setStockActual_WithZero_ShouldSetValue() {
        // Act
        product.setStockActual(0);

        // Assert
        assertEquals(0, product.getStockActual());
    }

    @Test
    void setName_WithEmptyString_ShouldSetValue() {
        // Act
        product.setName("");

        // Assert - Entity allows it, validation is in DTO/Service layer
        assertEquals("", product.getName());
    }

    @Test
    void stockComparison_WhenActualBelowMinimum_ShouldBeDetectable() {
        // Arrange
        product.setStockMinimo(20);
        product.setStockActual(10);

        // Assert
        assertTrue(product.getStockActual() < product.getStockMinimo());
    }

    @Test
    void stockComparison_WhenActualAboveMinimum_ShouldBeDetectable() {
        // Arrange
        product.setStockMinimo(10);
        product.setStockActual(50);

        // Assert
        assertTrue(product.getStockActual() > product.getStockMinimo());
    }
}

