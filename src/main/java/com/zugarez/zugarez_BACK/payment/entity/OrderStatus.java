package com.zugarez.zugarez_BACK.payment.entity;

/**
 * Enumeration representing the various states of an order in the payment lifecycle.
 * Tracks order status from pending to final states like approved, rejected, or refunded.
 */
public enum OrderStatus {
    /**
     * Payment initiated but not yet confirmed.
     */
    PENDING,

    /**
     * Payment approved and confirmed successfully.
     */
    APPROVED,

    /**
     * Payment rejected by the payment processor.
     */
    REJECTED,

    /**
     * Error occurred during payment processing.
     */
    FAILED,

    /**
     * Order cancelled by the user.
     */
    CANCELLED,

    /**
     * Payment has been refunded to the customer.
     */
    REFUNDED
}
