package com.zugarez.zugarez_BACK.payment.entity;

public enum OrderStatus {
    PENDING,      // Pago iniciado pero no confirmado
    APPROVED,     // ✅ Pago aprobado y confirmado
    REJECTED,     // Pago rechazado
    FAILED,       // Error en el procesamiento
    CANCELLED,    // Cancelado por el usuario
    REFUNDED      // Reembolsado
}
