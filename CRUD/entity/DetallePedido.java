package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

/**
 * Entity representing an order detail (detalle de pedido) in the system.
 * Contains details such as order ID, lot ID, quantity, and subtotal.
 */
@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    /** Order detail ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_detalle;
    /** Order ID associated with this detail */
    private Integer id_pedido;
    /** Lot ID associated with this detail */
    private Integer id_lote;
    /** Quantity of items in this detail */
    private Integer cantidad;
    /** Subtotal for this detail */
    private Double subtotal;

    /**
     * Gets the order detail ID.
     * @return Order detail ID
     */
    public Integer getId_detalle() {
        return id_detalle;
    }

    /**
     * Sets the order detail ID.
     * @param id_detalle Order detail ID
     */
    public void setId_detalle(Integer id_detalle) {
        this.id_detalle = id_detalle;
    }

    /**
     * Gets the order ID associated with this detail.
     * @return Order ID
     */
    public Integer getId_pedido() {
        return id_pedido;
    }

    /**
     * Sets the order ID associated with this detail.
     * @param id_pedido Order ID
     */
    public void setId_pedido(Integer id_pedido) {
        this.id_pedido = id_pedido;
    }

    /**
     * Gets the lot ID associated with this detail.
     * @return Lot ID
     */
    public Integer getId_lote() {
        return id_lote;
    }

    /**
     * Sets the lot ID associated with this detail.
     * @param id_lote Lot ID
     */
    public void setId_lote(Integer id_lote) {
        this.id_lote = id_lote;
    }

    /**
     * Gets the quantity of items in this detail.
     * @return Quantity
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Sets the quantity of items in this detail.
     * @param cantidad Quantity
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Gets the subtotal for this detail.
     * @return Subtotal
     */
    public Double getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal for this detail.
     * @param subtotal Subtotal
     */
    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}
