package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

/**
 * Entity representing a payment method in the system.
 * Contains details such as the payment method concept.
 */
@Entity
@Table(name = "metodo_pago")
public class MetodoPago {
    /** Payment method ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_metodo;
    /** Concept or name of the payment method */
    private String concepto;

    /**
     * Gets the payment method ID.
     * @return Payment method ID
     */
    public Integer getId_metodo() {
        return id_metodo;
    }

    /**
     * Sets the payment method ID.
     * @param id_metodo Payment method ID
     */
    public void setId_metodo(Integer id_metodo) {
        this.id_metodo = id_metodo;
    }

    /**
     * Gets the concept or name of the payment method.
     * @return Payment method concept
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Sets the concept or name of the payment method.
     * @param concepto Payment method concept
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
}
