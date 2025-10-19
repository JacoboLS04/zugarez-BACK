package com.zugarez.zugarez_BACK.CRUD.repository;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRepository.
 * Tests database queries and custom repository methods.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("Test Product Repository");
        testProduct.setPrice(150.0);
        testProduct.setBrand("Test Brand Repo");
        testProduct.setSupplierId(1);
        testProduct.setDescription("Repository Test Description");
        testProduct.setUrlImage("http://test.com/repo-image.jpg");
        testProduct.setStockMinimo(5);
        testProduct.setStockActual(25);
    }

    @Test
    void save_ShouldPersistProduct() {
        // Act
        Product savedProduct = productRepository.save(testProduct);
        entityManager.flush();

        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("Test Product Repository", savedProduct.getName());
        assertEquals(150.0, savedProduct.getPrice());
    }

    @Test
    void findById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        Product savedProduct = entityManager.persistAndFlush(testProduct);

        // Act
        Optional<Product> found = productRepository.findById(savedProduct.getId());

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Product Repository", found.get().getName());
    }

    @Test
    void findById_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Product> found = productRepository.findById(9999);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void existsByName_WhenProductExists_ShouldReturnTrue() {
        // Arrange
        entityManager.persistAndFlush(testProduct);

        // Act
        boolean exists = productRepository.existsByName("Test Product Repository");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByName_WhenProductDoesNotExist_ShouldReturnFalse() {
        // Act
        boolean exists = productRepository.existsByName("Non-existent Product");

        // Assert
        assertFalse(exists);
    }

    @Test
    void findByName_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        entityManager.persistAndFlush(testProduct);

        // Act
        Optional<Product> found = productRepository.findByName("Test Product Repository");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Test Product Repository", found.get().getName());
        assertEquals(150.0, found.get().getPrice());
    }

    @Test
    void findByName_WhenProductDoesNotExist_ShouldReturnEmpty() {
        // Act
        Optional<Product> found = productRepository.findByName("Non-existent Product");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findAllExplicit_ShouldReturnAllProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(100.0);
        product1.setBrand("Brand 1");
        product1.setSupplierId(1);
        product1.setDescription("Description 1");
        product1.setUrlImage("http://test.com/1.jpg");
        product1.setStockMinimo(10);
        product1.setStockActual(50);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(200.0);
        product2.setBrand("Brand 2");
        product2.setSupplierId(2);
        product2.setDescription("Description 2");
        product2.setUrlImage("http://test.com/2.jpg");
        product2.setStockMinimo(15);
        product2.setStockActual(60);

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        // Act
        List<Product> products = productRepository.findAllExplicit();

        // Assert
        assertNotNull(products);
        assertTrue(products.size() >= 2);
        assertTrue(products.stream().anyMatch(p -> "Product 1".equals(p.getName())));
        assertTrue(products.stream().anyMatch(p -> "Product 2".equals(p.getName())));
    }

    @Test
    void delete_ShouldRemoveProduct() {
        // Arrange
        Product savedProduct = entityManager.persistAndFlush(testProduct);
        int productId = savedProduct.getId();

        // Act
        productRepository.delete(savedProduct);
        entityManager.flush();

        // Assert
        Optional<Product> found = productRepository.findById(productId);
        assertFalse(found.isPresent());
    }

    @Test
    void update_ShouldModifyProduct() {
        // Arrange
        Product savedProduct = entityManager.persistAndFlush(testProduct);

        // Act
        savedProduct.setName("Updated Product Name");
        savedProduct.setPrice(250.0);
        Product updatedProduct = productRepository.save(savedProduct);
        entityManager.flush();

        // Assert
        Optional<Product> found = productRepository.findById(updatedProduct.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Product Name", found.get().getName());
        assertEquals(250.0, found.get().getPrice());
    }
}

