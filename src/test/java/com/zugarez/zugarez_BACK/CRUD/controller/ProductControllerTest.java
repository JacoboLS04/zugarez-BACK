package com.zugarez.zugarez_BACK.CRUD.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zugarez.zugarez_BACK.CRUD.dto.ProductDto;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.service.ProductService;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController.
 * Tests REST endpoints with security and validation.
 */
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("deprecation")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(roles = "USER")
    void getAll_WithUserRole_ShouldReturnProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(testProduct));

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(100.0));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    void getAll_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());

        verify(productService, never()).getAllProducts();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOne_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1)).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Product"));

        verify(productService, times(1)).getProductById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOne_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_WithAdminRole_ShouldCreateProduct() throws Exception {
        // Arrange
        when(productService.saveProduct(any(ProductDto.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(productService, times(1)).saveProduct(any(ProductDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void save_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isForbidden());

        verify(productService, never()).saveProduct(any(ProductDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ProductDto invalidDto = new ProductDto();
        invalidDto.setName(""); // Empty name should fail validation
        invalidDto.setPrice(-10); // Negative price should fail

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).saveProduct(any(ProductDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void save_WhenNameExists_ShouldReturnBadRequest() throws Exception {
        // Arrange
        when(productService.saveProduct(any(ProductDto.class)))
                .thenThrow(new AttributeException("Ya existe un producto con ese nombre"));

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).saveProduct(any(ProductDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_WithAdminRole_ShouldUpdateProduct() throws Exception {
        // Arrange
        when(productService.updateProduct(eq(1), any(ProductDto.class))).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(productService, times(1)).updateProduct(eq(1), any(ProductDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProductDto)))
                .andExpect(status().isForbidden());

        verify(productService, never()).updateProduct(anyInt(), any(ProductDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WithAdminRole_ShouldDeleteProduct() throws Exception {
        // Arrange
        when(productService.deleteProduct(1)).thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());

        verify(productService, times(1)).deleteProduct(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_WithUserRole_ShouldReturnForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isForbidden());

        verify(productService, never()).deleteProduct(anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_WhenProductDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Arrange
        when(productService.deleteProduct(999))
                .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        // Act & Assert
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(999);
    }
}
