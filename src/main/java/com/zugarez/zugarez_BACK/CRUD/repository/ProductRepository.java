package com.zugarez.zugarez_BACK.CRUD.repository;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    boolean existsByName(String name);
    Optional<Product> findByName(String name);
}
