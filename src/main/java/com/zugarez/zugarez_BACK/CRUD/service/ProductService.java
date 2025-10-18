package com.zugarez.zugarez_BACK.CRUD.service;

import com.zugarez.zugarez_BACK.CRUD.dto.ProductDto;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
// import com.zugarez.zugarez_BACK.global.utils.Operations;
// import io.micrometer.core.instrument.Counter;
// import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Service class for managing products.
 * Provides business logic for CRUD operations on Product entities.
 */
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    // @Autowired
    // private Counter productCreatedCounter;

    // @Autowired
    // private Timer databaseQueryTimer;

    /**
     * Retrieves all products from the database.
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        // Timer.Sample sample = Timer.start();
        try {
            System.out.println("🔍 DEBUG - Iniciando getAllProducts()");
            
            List<Product> products = productRepository.findAllExplicit();
            
            System.out.println("🔍 DEBUG - Productos encontrados: " + products.size());
            
            for (Product p : products) {
                System.out.println("=== PRODUCTO DEBUG ===");
                System.out.println("ID: " + p.getId());
                System.out.println("Name: " + p.getName());
                System.out.println("Price: " + p.getPrice());
                System.out.println("Brand: " + p.getBrand());
                System.out.println("SupplierId: " + p.getSupplierId());
                System.out.println("Description: " + p.getDescription());
                System.out.println("UrlImage: " + p.getUrlImage());
                System.out.println("=====================");
            }
            
            return products;
        } finally {
            // sample.stop(databaseQueryTimer);
        }
    }

    /**
     * Retrieves a product by its ID.
     * @param id Product ID
     * @return Product with the given ID
     * @throws ResourceNotFoundException if the product is not found
     */
    public Product getProductById(int id) throws ResourceNotFoundException {
        return productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Producto no encontrado"));
    }

    /**
     * Saves a new product to the database.
     * @param dto ProductDto containing product data
     * @return The saved Product entity
     * @throws AttributeException if a product with the same name already exists
     */
    public Product saveProduct(ProductDto dto) throws AttributeException {
        if(productRepository.existsByName(dto.getName())) {
            throw new AttributeException("Ya existe un producto con ese nombre");
        }
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setBrand(dto.getBrand());
        product.setSupplierId(dto.getSupplierId());
        product.setDescription(dto.getDescription());
        product.setUrlImage(dto.getUrlImage());
        product.setStockMinimo(dto.getStockMinimo());
        product.setStockActual(dto.getStockActual());
        System.out.println("Se intenta guardar el producto: " + dto.getName());

        Product savedProduct = productRepository.save(product);
        // productCreatedCounter.increment();
        return savedProduct;
    }

    /**
     * Updates an existing product in the database.
     * @param id Product ID
     * @param dto ProductDto containing updated data
     * @return The updated Product entity
     * @throws ResourceNotFoundException if the product is not found
     * @throws AttributeException if a product with the same name already exists
     */
    public Product updateProduct(int id, ProductDto dto) throws ResourceNotFoundException, AttributeException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        if(productRepository.existsByName(dto.getName()) && productRepository.findByName(dto.getName()).get().getId() != id) {
            throw new AttributeException("Ya existe un producto con ese nombre");
        }
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setBrand(dto.getBrand());
        product.setSupplierId(dto.getSupplierId());
        product.setDescription(dto.getDescription());
        product.setUrlImage(dto.getUrlImage());
        product.setStockMinimo(dto.getStockMinimo());
        product.setStockActual(dto.getStockActual());
        return productRepository.save(product);
    }

    /**
     * Deletes a product from the database.
     * @param id Product ID
     * @return The deleted Product entity
     * @throws ResourceNotFoundException if the product is not found
     */
    public Product deleteProduct(int id) throws ResourceNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productRepository.delete(product);
        return product;
    }


}
