package com.zugarez.zugarez_BACK.CRUD.service;

import com.zugarez.zugarez_BACK.CRUD.dto.ProductDto;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Uses Mockito to mock dependencies.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setBrand("Test Brand");
        testProduct.setSupplierId(1);
        testProduct.setDescription("Test Description");
        testProduct.setUrlImage("http://test.com/image.jpg");
        testProduct.setStockMinimo(5);
        testProduct.setStockActual(10);

        testProductDto = new ProductDto();
        testProductDto.setName("New Product");
        testProductDto.setPrice(50.0);
        testProductDto.setBrand("New Brand");
        testProductDto.setSupplierId(2);
        testProductDto.setDescription("New Description");
        testProductDto.setUrlImage("http://test.com/new.jpg");
        testProductDto.setStockMinimo(3);
        testProductDto.setStockActual(8);
    }

    @Test
    void testGetAllProducts() {
        List<Product> products = Collections.singletonList(testProduct);
        when(productRepository.findAllExplicit()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAllExplicit();
    }

    @Test
    void testGetProductById_Success() throws ResourceNotFoundException {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById(1);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999);
        });
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    void testSaveProduct_Success() throws AttributeException {
        when(productRepository.existsByName(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.saveProduct(testProductDto);

        assertNotNull(result);
        verify(productRepository, times(1)).existsByName("New Product");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testSaveProduct_DuplicateName() {
        when(productRepository.existsByName("New Product")).thenReturn(true);

        assertThrows(AttributeException.class, () -> {
            productService.saveProduct(testProductDto);
        });
        verify(productRepository, times(1)).existsByName("New Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_Success() throws ResourceNotFoundException, AttributeException {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsByName("New Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(1, testProductDto);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(999, testProductDto);
        });
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).save(any(Product.class));
    }

   // File: `src/test/java/com/zugarez/zugarez_BACK/CRUD/service/ProductServiceTest.java`
    @Test
    void testDeleteProduct_Success() throws ResourceNotFoundException {
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct); // <-- changed

        Product result = productService.deleteProduct(1);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(testProduct); // <-- changed
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999);
        });
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).deleteById(anyInt());
    }
}

