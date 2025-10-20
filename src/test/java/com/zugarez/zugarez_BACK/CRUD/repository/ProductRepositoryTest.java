package com.zugarez.zugarez_BACK.CRUD.repository;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ProductRepository.
 * Tests repository methods with an in-memory database.
 */
@DataJpaTest
@ActiveProfiles("test")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setBrand("Test Brand");
        testProduct.setSupplierId(1);
        testProduct.setDescription("Test Description");
        testProduct.setUrlImage("http://test.com/image.jpg");
        testProduct.setStockMinimo(5);
        testProduct.setStockActual(10);
    }

    @Test
    void testSaveProduct() {
        Product saved = productRepository.save(testProduct);

        assertNotNull(saved);
        assertTrue(saved.getId() > 0);
        assertEquals("Test Product", saved.getName());
    }

    @Test
    void testFindById() {
        Product saved = productRepository.save(testProduct);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getName(), found.get().getName());
    }

    @Test
    void testExistsByName() {
        productRepository.save(testProduct);

        assertTrue(productRepository.existsByName("Test Product"));
        assertFalse(productRepository.existsByName("Non Existent Product"));
    }

    @Test
    void testFindByName() {
        productRepository.save(testProduct);

        Optional<Product> found = productRepository.findByName("Test Product");

        assertTrue(found.isPresent());
        assertEquals("Test Product", found.get().getName());
    }

    @Test
    void testFindAllExplicit() {
        productRepository.save(testProduct);

        Product anotherProduct = new Product();
        anotherProduct.setName("Another Product");
        anotherProduct.setPrice(50.0);
        anotherProduct.setBrand("Another Brand");
        anotherProduct.setSupplierId(2);
        anotherProduct.setUrlImage("http://test.com/image2.jpg");
        anotherProduct.setStockMinimo(3);
        anotherProduct.setStockActual(8);
        productRepository.save(anotherProduct);

        List<Product> products = productRepository.findAllExplicit();

        assertEquals(2, products.size());
    }

    @Test
    void testDeleteProduct() {
        Product saved = productRepository.save(testProduct);
        int id = saved.getId();

        productRepository.deleteById(id);

        Optional<Product> found = productRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void testUpdateProduct() {
        Product saved = productRepository.save(testProduct);

        saved.setPrice(150.0);
        saved.setName("Updated Product");
        Product updated = productRepository.save(saved);

        assertEquals(150.0, updated.getPrice());
        assertEquals("Updated Product", updated.getName());
    }
}
