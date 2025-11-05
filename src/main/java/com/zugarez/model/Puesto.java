package com.zugarez.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "puestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Puesto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    @Column(name = "salario_minimo", precision = 10, scale = 2)
    private BigDecimal salarioMinimo;

    @Column(name = "salario_maximo", precision = 10, scale = 2)
    private BigDecimal salarioMaximo;
}
