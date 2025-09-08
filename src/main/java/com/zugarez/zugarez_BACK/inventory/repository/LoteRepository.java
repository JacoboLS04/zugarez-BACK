package com.zugarez.zugarez_BACK.inventory.repository;

import com.zugarez.zugarez_BACK.inventory.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Lote (batch) entities.
 * Provides methods for querying batches by product, availability, expiration, and custom queries for stock and ordering.
 */
@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    /**
     * Finds all batches for a given product.
     * @param productId Product ID
     * @return List of batches for the product
     */
    List<Lote> findByProductId(int productId);

    /**
     * Finds all batches with available quantity greater than the specified value.
     * @param quantity Minimum available quantity
     * @return List of available batches
     */
    List<Lote> findByAvailableQuantityGreaterThan(int quantity);

    /**
     * Finds all batches that expire before the specified date.
     * @param date Expiration date limit
     * @return List of batches expiring before the date
     */
    List<Lote> findByExpirationDateBefore(LocalDate date);

    /**
     * Finds all available batches for a product (with available quantity > 0).
     * @param productId Product ID
     * @return List of available batches for the product
     */
    @Query("SELECT l FROM Lote l WHERE l.product.id = :productId AND l.availableQuantity > 0")
    List<Lote> findAvailableLotesByProduct(@Param("productId") int productId);

    /**
     * Finds all batches ordered by expiration date ascending.
     * @return List of batches ordered by expiration date
     */
    @Query("SELECT l FROM Lote l ORDER BY l.expirationDate ASC")
    List<Lote> findAllOrderByExpirationDate();

    /**
     * Gets the total available quantity for a product.
     * @param productId Product ID
     * @return Total available quantity
     */
    @Query("SELECT COALESCE(SUM(l.availableQuantity), 0) FROM Lote l WHERE l.product.id = :productId")
    int getTotalAvailableQuantityByProduct(@Param("productId") int productId);
}
