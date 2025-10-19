package com.zugarez.zugarez_BACK.payment.entity;

public enum OrderStatus {
    PENDING,        // Pendiente de pago
    PAID,           // Pagado
    FAILED,         // Pago fallido
    CANCELLED,      // Cancelado
    PROCESSING,     // En proceso
    COMPLETED       // Completado (entregado)
}
