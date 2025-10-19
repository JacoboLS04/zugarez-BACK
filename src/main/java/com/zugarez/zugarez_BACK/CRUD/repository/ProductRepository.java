package com.zugarez.zugarez_BACK.CRUD.repository;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entities.
 * Provides methods for querying products by name and custom queries for explicit and native retrieval.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    /**
     * Checks if a product exists by its name.
     * @param name Product name
     * @return true if a product with the given name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a product by its name.
     * @param name Product name
     * @return Optional containing the product if found, or empty if not found
     */
    Optional<Product> findByName(String name);

    /**
     * Retrieves all products using an explicit JPQL query.
     * @return List of all products
     */
    @Query("SELECT p FROM Product p")
    List<Product> findAllExplicit();

    /**
     * Retrieves all products using a native SQL query (for debugging).
     * @return List of product records as Object arrays
     */
    @Query(value = "SELECT * FROM products", nativeQuery = true)
    List<Object[]> findAllNative();
}
