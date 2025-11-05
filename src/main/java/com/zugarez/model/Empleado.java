package com.zugarez.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String dni;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;

    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;

    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_contrato", nullable = false)
    private TipoContrato tipoContrato;

    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;

    private String banco;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "puesto_id")
    private Puesto puesto;
}
