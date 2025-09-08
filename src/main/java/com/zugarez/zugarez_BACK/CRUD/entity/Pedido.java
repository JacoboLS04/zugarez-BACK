package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity representing an order (pedido) in the system.
 * Contains order details such as client ID, order date, status, and total amount.
 */
@Entity
@Table(name = "pedido")
public class Pedido {
    /** Order ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pedido;
    /** Client ID associated with the order */
    private Integer id_cliente;
    /** Date when the order was placed */
    private Date fecha_pedido;
    /** Status ID of the order */
    private String id_estado_pedido;
    /** Total amount of the order */
    private Double total_pedido;

    /**
     * Gets the order ID.
     * @return Order ID
     */
    public Integer getId_pedido() {
        return id_pedido;
    }

    /**
     * Sets the order ID.
     * @param id_pedido Order ID
     */
    public void setId_pedido(Integer id_pedido) {
        this.id_pedido = id_pedido;
    }

    /**
     * Gets the client ID.
     * @return Client ID
     */
    public Integer getId_cliente() {
        return id_cliente;
    }

    /**
     * Sets the client ID.
     * @param id_cliente Client ID
     */
    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }

    /**
     * Gets the order date.
     * @return Order date
     */
    public Date getFecha_pedido() {
        return fecha_pedido;
    }

    /**
     * Sets the order date.
     * @param fecha_pedido Order date
     */
    public void setFecha_pedido(Date fecha_pedido) {
        this.fecha_pedido = fecha_pedido;
    }

    /**
     * Gets the order status ID.
     * @return Order status ID
     */
    public String getId_estado_pedido() {
        return id_estado_pedido;
    }

    /**
     * Sets the order status ID.
     * @param id_estado_pedido Order status ID
     */
    public void setId_estado_pedido(String id_estado_pedido) {
        this.id_estado_pedido = id_estado_pedido;
    }

    /**
     * Gets the total amount of the order.
     * @return Total order amount
     */
    public Double getTotal_pedido() {
        return total_pedido;
    }

    /**
     * Sets the total amount of the order.
     * @param total_pedido Total order amount
     */
    public void setTotal_pedido(Double total_pedido) {
        this.total_pedido = total_pedido;
    }
}
