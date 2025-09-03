package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_movimiento")
public class TipoMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_tipo;
    private String concepto;

    // Getters y setters
}
