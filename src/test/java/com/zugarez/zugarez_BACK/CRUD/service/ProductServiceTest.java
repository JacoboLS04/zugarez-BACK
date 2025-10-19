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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Tests business logic for product CRUD operations.
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
        testProduct.setPrice(100.0);
        testProduct.setBrand("Test Brand");
        testProduct.setSupplierId(1);
        testProduct.setDescription("Test Description");
        testProduct.setUrlImage("http://test.com/image.jpg");
        testProduct.setStockMinimo(10);
        testProduct.setStockActual(50);

        testProductDto = new ProductDto();
        testProductDto.setName("Test Product");
        testProductDto.setPrice(100.0);
        testProductDto.setBrand("Test Brand");
        testProductDto.setSupplierId(1);
        testProductDto.setDescription("Test Description");
        testProductDto.setUrlImage("http://test.com/image.jpg");
        testProductDto.setStockMinimo(10);
        testProductDto.setStockActual(50);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAllExplicit()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAllExplicit();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() throws ResourceNotFoundException {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999);
        });
        verify(productRepository, times(1)).findById(999);
    }

    @Test
    void saveProduct_WhenProductNameIsUnique_ShouldSaveProduct() throws AttributeException {
        // Arrange
        when(productRepository.existsByName("Test Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.saveProduct(testProductDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(100.0, result.getPrice());
        verify(productRepository, times(1)).existsByName("Test Product");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void saveProduct_WhenProductNameExists_ShouldThrowException() {
        // Arrange
        when(productRepository.existsByName("Test Product")).thenReturn(true);

        // Act & Assert
        assertThrows(AttributeException.class, () -> {
            productService.saveProduct(testProductDto);
        });
        verify(productRepository, times(1)).existsByName("Test Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateProduct() throws ResourceNotFoundException, AttributeException {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsByName("Test Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Product result = productService.updateProduct(1, testProductDto);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProduct(999, testProductDto);
        });
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenNameExistsForDifferentProduct_ShouldThrowException() {
        // Arrange
        Product existingProduct = new Product();
        existingProduct.setId(2);
        existingProduct.setName("Test Product");

        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        when(productRepository.existsByName("Test Product")).thenReturn(true);
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        assertThrows(AttributeException.class, () -> {
            productService.updateProduct(1, testProductDto);
        });
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() throws ResourceNotFoundException {
        // Arrange
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        // Act
        Product result = productService.deleteProduct(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999);
        });
        verify(productRepository, times(1)).findById(999);
        verify(productRepository, never()).delete(any(Product.class));
    }
}

