package com.zugarez.zugarez_BACK.CRUD.repository;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByName(String name);
    Optional<Product> findByName(String name);
    
    // Consulta explícita para forzar mapeo correcto
    @Query("SELECT p FROM Product p")
    List<Product> findAllExplicit();
    
    // Consulta nativa para debug
    @Query(value = "SELECT * FROM products", nativeQuery = true)
    List<Object[]> findAllNative();
}
