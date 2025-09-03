package com.zugarez.zugarez_BACK.global.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cargo")
public class Cargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_cargo;
    private String nombre_cargo;

    // Getters y setters
}
