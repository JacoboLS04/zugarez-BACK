package com.zugarez.zugarez_BACK.CRUD.service;

import com.zugarez.zugarez_BACK.CRUD.dto.ProductDto;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.exceptions.ResourceNotFoundException;
import com.zugarez.zugarez_BACK.global.utils.Operations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public List<Product> getAllProducts() {
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
    }

    public Product getProductById(int id) throws ResourceNotFoundException {
        return productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Producto no encontrado"));
    }

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
        System.out.println("Se intenta guardar el producto: " + dto.getName());

        return productRepository.save(product);
    }

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
        return productRepository.save(product);
    }

    public Product deleteProduct(int id) throws ResourceNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productRepository.delete(product);
        return product;
    }


}
