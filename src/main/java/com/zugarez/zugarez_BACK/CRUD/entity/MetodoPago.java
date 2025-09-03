package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "metodo_pago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_metodo;
    private String concepto;

    // Getters y setters
}
