package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

/**
 * Entity representing a payment for an order (pago de pedido) in the system.
 * Contains details such as order ID, amount, and payment method ID.
 */
@Entity
@Table(name = "pago_pedido")
public class PagoPedido {
    /** Payment ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pago;
    /** Order ID associated with the payment */
    private Integer id_pedido;
    /** Payment amount */
    private Double monto;
    /** Payment method ID */
    private Integer id_metodo_pago;

    /**
     * Gets the payment ID.
     * @return Payment ID
     */
    public Integer getId_pago() {
        return id_pago;
    }

    /**
     * Sets the payment ID.
     * @param id_pago Payment ID
     */
    public void setId_pago(Integer id_pago) {
        this.id_pago = id_pago;
    }

    /**
     * Gets the order ID associated with the payment.
     * @return Order ID
     */
    public Integer getId_pedido() {
        return id_pedido;
    }

    /**
     * Sets the order ID associated with the payment.
     * @param id_pedido Order ID
     */
    public void setId_pedido(Integer id_pedido) {
        this.id_pedido = id_pedido;
    }

    /**
     * Gets the payment amount.
     * @return Payment amount
     */
    public Double getMonto() {
        return monto;
    }

    /**
     * Sets the payment amount.
     * @param monto Payment amount
     */
    public void setMonto(Double monto) {
        this.monto = monto;
    }

    /**
     * Gets the payment method ID.
     * @return Payment method ID
     */
    public Integer getId_metodo_pago() {
        return id_metodo_pago;
    }

    /**
     * Sets the payment method ID.
     * @param id_metodo_pago Payment method ID
     */
    public void setId_metodo_pago(Integer id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }
}
