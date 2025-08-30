package com.zugarez.zugarez_BACK.inventory.repository;

import com.zugarez.zugarez_BACK.inventory.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    
    // Buscar lotes por producto
    List<Lote> findByProductId(int productId);
    
    // Buscar lotes con cantidad disponible mayor a 0
    List<Lote> findByAvailableQuantityGreaterThan(int quantity);
    
    // Buscar lotes próximos a vencer
    List<Lote> findByExpirationDateBefore(LocalDate date);
    
    // Buscar lotes por producto y que tengan stock disponible
    @Query("SELECT l FROM Lote l WHERE l.product.id = :productId AND l.availableQuantity > 0")
    List<Lote> findAvailableLotesByProduct(@Param("productId") int productId);
    
    // Buscar lotes ordenados por fecha de vencimiento
    @Query("SELECT l FROM Lote l ORDER BY l.expirationDate ASC")
    List<Lote> findAllOrderByExpirationDate();
    
    // Obtener el stock total disponible de un producto
    @Query("SELECT COALESCE(SUM(l.availableQuantity), 0) FROM Lote l WHERE l.product.id = :productId")
    int getTotalAvailableQuantityByProduct(@Param("productId") int productId);
}
