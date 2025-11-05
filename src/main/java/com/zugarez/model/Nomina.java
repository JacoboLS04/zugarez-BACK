package com.zugarez.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nominas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nomina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas;

    @Column(name = "horas_extras")
    private Double horasExtras;

    @Column(name = "pago_horas_extras", precision = 10, scale = 2)
    private BigDecimal pagoHorasExtras;

    @Column(precision = 10, scale = 2)
    private BigDecimal comisiones;

    @Column(precision = 10, scale = 2)
    private BigDecimal bonificaciones;

    @Column(name = "total_ingresos", precision = 10, scale = 2)
    private BigDecimal totalIngresos;

    @Column(name = "deduccion_essalud", precision = 10, scale = 2)
    private BigDecimal deduccionEssalud;

    @Column(name = "deduccion_onp", precision = 10, scale = 2)
    private BigDecimal deduccionOnp;

    @Column(name = "deduccion_afp", precision = 10, scale = 2)
    private BigDecimal deduccionAfp;

    @Column(name = "otras_deducciones", precision = 10, scale = 2)
    private BigDecimal otrasDeducciones;

    @Column(name = "total_deducciones", precision = 10, scale = 2)
    private BigDecimal totalDeducciones;

    @Column(name = "salario_neto", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioNeto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNomina estado;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    @Column(name = "numero_transaccion")
    private String numeroTransaccion;

    private String observaciones;
}
