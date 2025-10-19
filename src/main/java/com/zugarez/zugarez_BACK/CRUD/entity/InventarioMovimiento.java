package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity representing an inventory movement in the system.
 * Contains details such as lot ID, quantity, reason, date, and movement type.
 */
@Entity
@Table(name = "inventario_movimientos")
public class InventarioMovimiento {
    /** Inventory movement ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_movimiento;
    /** Lot ID associated with the movement */
    private Integer id_lote;
    /** Quantity moved */
    private Integer cantidad;
    /** Reason for the movement */
    private String motivo;
    /** Date of the movement */
    private Date fecha_movimiento;
    /** Type of movement ID */
    private Integer id_tipo_movimiento;

    /**
     * Gets the inventory movement ID.
     * @return Inventory movement ID
     */
    public Integer getId_movimiento() {
        return id_movimiento;
    }

    /**
     * Sets the inventory movement ID.
     * @param id_movimiento Inventory movement ID
     */
    public void setId_movimiento(Integer id_movimiento) {
        this.id_movimiento = id_movimiento;
    }

    /**
     * Gets the lot ID.
     * @return Lot ID
     */
    public Integer getId_lote() {
        return id_lote;
    }

    /**
     * Sets the lot ID.
     * @param id_lote Lot ID
     */
    public void setId_lote(Integer id_lote) {
        this.id_lote = id_lote;
    }

    /**
     * Gets the quantity moved.
     * @return Quantity
     */
    public Integer getCantidad() {
        return cantidad;
    }

    /**
     * Sets the quantity moved.
     * @param cantidad Quantity
     */
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Gets the reason for the movement.
     * @return Reason
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * Sets the reason for the movement.
     * @param motivo Reason
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * Gets the date of the movement.
     * @return Movement date
     */
    public Date getFecha_movimiento() {
        return fecha_movimiento;
    }

    /**
     * Sets the date of the movement.
     * @param fecha_movimiento Movement date
     */
    public void setFecha_movimiento(Date fecha_movimiento) {
        this.fecha_movimiento = fecha_movimiento;
    }

    /**
     * Gets the movement type ID.
     * @return Movement type ID
     */
    public Integer getId_tipo_movimiento() {
        return id_tipo_movimiento;
    }

    /**
     * Sets the movement type ID.
     * @param id_tipo_movimiento Movement type ID
     */
    public void setId_tipo_movimiento(Integer id_tipo_movimiento) {
        this.id_tipo_movimiento = id_tipo_movimiento;
    }
}
