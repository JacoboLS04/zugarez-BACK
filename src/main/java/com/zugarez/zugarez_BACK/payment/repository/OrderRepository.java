package com.zugarez.zugarez_BACK.payment.repository;

import com.zugarez.zugarez_BACK.payment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entities.
 * Provides methods for querying orders by user ID and MercadoPago identifiers.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    /**
     * Finds all orders for a specific user.
     * @param userId the user ID
     * @return list of orders
     */
    List<Order> findByUserId(Integer userId);

    /**
     * Finds an order by its MercadoPago preference ID.
     * @param preferenceId the MercadoPago preference ID
     * @return Optional containing the order if found
     */
    Optional<Order> findByMercadopagoPreferenceId(String preferenceId);

    /**
     * Finds an order by its MercadoPago payment ID.
     * @param paymentId the MercadoPago payment ID
     * @return Optional containing the order if found
     */
    Optional<Order> findByMercadopagoPaymentId(String paymentId);
}
