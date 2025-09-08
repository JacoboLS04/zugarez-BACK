package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

/**
 * Entity representing a type of inventory movement in the system.
 * Contains details such as the movement concept.
 */
@Entity
@Table(name = "tipo_movimiento")
public class TipoMovimiento {
    /** Movement type ID (Primary Key) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_tipo;
    /** Concept or name of the movement type */
    private String concepto;

    /**
     * Gets the movement type ID.
     * @return Movement type ID
     */
    public Integer getId_tipo() {
        return id_tipo;
    }

    /**
     * Sets the movement type ID.
     * @param id_tipo Movement type ID
     */
    public void setId_tipo(Integer id_tipo) {
        this.id_tipo = id_tipo;
    }

    /**
     * Gets the concept or name of the movement type.
     * @return Movement type concept
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Sets the concept or name of the movement type.
     * @param concepto Movement type concept
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
}
