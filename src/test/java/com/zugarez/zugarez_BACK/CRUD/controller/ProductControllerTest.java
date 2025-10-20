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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductController.
 * Tests REST endpoints with mocked security and service layer.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
    @WithMockUser(roles = "USER")
    void testGetAllProducts() throws Exception {
        List<Product> products = List.of(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Test Product"))
            .andExpect(jsonPath("$[0].price").value(99.99));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductById() throws Exception {
        when(productService.getProductById(1)).thenReturn(testProduct);

        mockMvc.perform(get("/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Test Product"))
            .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductById_NotFound() throws Exception {
        when(productService.getProductById(999))
            .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

        mockMvc.perform(get("/products/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct() throws Exception {
        when(productService.saveProduct(any(ProductDto.class))).thenReturn(testProduct);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct_DuplicateName() throws Exception {
        when(productService.saveProduct(any(ProductDto.class)))
            .thenThrow(new AttributeException("Ya existe un producto con ese nombre"));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(eq(1), any(ProductDto.class))).thenReturn(testProduct);

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteProduct() throws Exception {
        when(productService.deleteProduct(1)).thenReturn(testProduct);

        mockMvc.perform(delete("/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGetAllProducts_Unauthorized() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateProduct_Forbidden() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDto)))
            .andExpect(status().isForbidden());
    }
}
