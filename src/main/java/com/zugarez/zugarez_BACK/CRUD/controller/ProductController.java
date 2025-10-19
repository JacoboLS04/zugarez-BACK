package com.zugarez.zugarez_BACK.CRUD.controller;

import com.zugarez.zugarez_BACK.CRUD.dto.ProductDto;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.CRUD.service.ProductService;
import com.zugarez.zugarez_BACK.global.dto.MessageDto;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

    /**
 * REST controller for managing products.
 * Provides endpoints for CRUD operations and debugging on Product entities.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves all products.
     * @return List of all products
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Retrieves a product by its ID.
     * @param id Product ID
     * @return The product with the given ID
     * @throws ResourceNotFoundException if the product is not found
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable("id") int id) throws ResourceNotFoundException {
        System.out.println("🔍 DEBUG - Obteniendo producto con ID: " + id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Creates a new product.
     * @param dto ProductDto with product data
     * @return MessageDto with creation status
     * @throws AttributeException if a product with the same name already exists
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MessageDto> save(@Valid @RequestBody ProductDto dto) throws AttributeException {
        Product product = productService.saveProduct(dto);
        String message = "Producto " + product.getName() + " creado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    /**
     * Updates an existing product.
     * @param id Product ID
     * @param dto ProductDto with updated data
     * @return MessageDto with update status
     * @throws ResourceNotFoundException if the product is not found
     * @throws AttributeException if a product with the same name already exists
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable("id") int id, @Valid @RequestBody ProductDto dto) throws ResourceNotFoundException, AttributeException {
        Product product = productService.updateProduct(id, dto);
        String message = "Producto " + product.getName() + " actualizado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    /**
     * Deletes a product by its ID.
     * @param id Product ID
     * @return MessageDto with deletion status
     * @throws ResourceNotFoundException if the product is not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> delete(@PathVariable("id") int id) throws ResourceNotFoundException {
        Product product = productService.deleteProduct(id);
        String message = "Producto " + product.getName() + " eliminado correctamente";
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, message));
    }

    /**
     * Testing endpoint: creates a product and returns the full Product entity.
     * @param dto ProductDto with product data
     * @return The created Product entity
     * @throws AttributeException if a product with the same name already exists
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/test")
    public ResponseEntity<Product> saveAndReturn(@Valid @RequestBody ProductDto dto) throws AttributeException {
        Product product = productService.saveProduct(dto);
        return ResponseEntity.ok(product);
    }

    /**
     * Debug endpoint: executes a native SQL query and returns the raw results.
     * @return List of Object arrays with product data
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/debug-sql")
    public ResponseEntity<?> debugSql() {
        System.out.println("🔍 DEBUG - Ejecutando consulta SQL nativa...");
        
        String sql = "SELECT id, name, price, brand, supplier_id, description, url_image FROM products";
        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
        
        System.out.println("🔍 DEBUG SQL DIRECTO - Resultados: " + results.size());
        for (Object[] row : results) {
            System.out.println("ID: " + row[0]);
            System.out.println("Name: " + row[1]);
            System.out.println("Price: " + row[2]);
            System.out.println("Brand: " + row[3]);
            System.out.println("Supplier_ID: " + row[4]);
            System.out.println("Description: " + row[5]);
            System.out.println("URL_Image: " + row[6]);
            System.out.println("------------------------");
        }
        
        return ResponseEntity.ok(results);
    }

}
