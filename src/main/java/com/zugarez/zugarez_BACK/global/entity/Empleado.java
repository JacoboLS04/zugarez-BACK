package com.zugarez.zugarez_BACK.global.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "empleado")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_empleado;
    private String nombre;
    private String apellido;
    private String cedula;
    private String email;
    private Integer id_cargo;

    // Getters y setters
}
