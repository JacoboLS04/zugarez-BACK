package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_supplier;
    private String name;
    private String email;
    private String address;

    // Getters y setters
}
