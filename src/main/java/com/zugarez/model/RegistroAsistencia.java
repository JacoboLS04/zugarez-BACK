package com.zugarez.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "registro_asistencia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAsistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalDateTime horaEntrada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Enumerated(EnumType.STRING)
    private TipoTurno turno;

    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas;

    @Column(name = "horas_extras")
    private Double horasExtras;

    private String observaciones;

    @PreUpdate
    @PrePersist
    public void calcularHoras() {
        if (horaEntrada != null && horaSalida != null) {
            Duration duration = Duration.between(horaEntrada, horaSalida);
            horasTrabajadas = duration.toMinutes() / 60.0;
            
            // Calcular horas extras (más de 8 horas)
            if (horasTrabajadas > 8) {
                horasExtras = horasTrabajadas - 8;
                horasTrabajadas = 8.0;
            } else {
                horasExtras = 0.0;
            }
        }
    }
}
