package com.zugarez.zugarez_BACK.asistencia.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "registro_asistencia")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private OffsetDateTime horaEntrada;

    @Column(name = "hora_salida")
    private OffsetDateTime horaSalida;

    @Column(name = "turno")
    private String turno;

    @Column(name = "horas_trabajadas")
    private Double horasTrabajadas;

    @Column(name = "horas_extras")
    private Double horasExtras;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (fecha == null) fecha = LocalDate.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public OffsetDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(OffsetDateTime horaEntrada) { this.horaEntrada = horaEntrada; }

    public OffsetDateTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(OffsetDateTime horaSalida) { this.horaSalida = horaSalida; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Double getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(Double horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }

    public Double getHorasExtras() { return horasExtras; }
    public void setHorasExtras(Double horasExtras) { this.horasExtras = horasExtras; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
