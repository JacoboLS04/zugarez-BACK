package com.zugarez.zugarez_BACK.nomina.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "nomina_calculos")
public class NominaCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(name = "empleado_nombre", length = 200)
    private String empleadoNombre;

    @Column(name = "inicio", nullable = false)
    private LocalDate inicio;

    @Column(name = "fin", nullable = false)
    private LocalDate fin;

    @Column(name = "dias", nullable = false)
    private Integer dias;

    @Column(name = "salario_base_mensual", nullable = false, precision = 19, scale = 2)
    private BigDecimal salarioBaseMensual;

    @Column(name = "horas_trabajadas", precision = 10, scale = 2)
    private BigDecimal horasTrabajadas;

    @Column(name = "horas_extras", precision = 10, scale = 2)
    private BigDecimal horasExtras;

    @Column(name = "pago_horas_extras", precision = 19, scale = 2)
    private BigDecimal pagoHorasExtras;

    @Column(name = "comisiones", precision = 19, scale = 2)
    private BigDecimal comisiones;

    @Column(name = "bonificaciones", precision = 19, scale = 2)
    private BigDecimal bonificaciones;

    @Column(name = "total_ingresos", precision = 19, scale = 2)
    private BigDecimal totalIngresos;

    @Column(name = "essalud", precision = 19, scale = 2)
    private BigDecimal essalud;

    @Column(name = "onp", precision = 19, scale = 2)
    private BigDecimal onp;

    @Column(name = "total_deducciones", precision = 19, scale = 2)
    private BigDecimal totalDeducciones;

    @Column(name = "neto_pagar", precision = 19, scale = 2)
    private BigDecimal netoPagar;

    @Column(name = "creado_en", updatable = false)
    private OffsetDateTime creadoEn;

    @PrePersist
    public void prePersist() {
        if (creadoEn == null) creadoEn = OffsetDateTime.now();
    }

    // Getters/Setters
    // ...existing code...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public String getEmpleadoNombre() { return empleadoNombre; }
    public void setEmpleadoNombre(String empleadoNombre) { this.empleadoNombre = empleadoNombre; }
    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }
    public LocalDate getFin() { return fin; }
    public void setFin(LocalDate fin) { this.fin = fin; }
    public Integer getDias() { return dias; }
    public void setDias(Integer dias) { this.dias = dias; }
    public BigDecimal getSalarioBaseMensual() { return salarioBaseMensual; }
    public void setSalarioBaseMensual(BigDecimal salarioBaseMensual) { this.salarioBaseMensual = salarioBaseMensual; }
    public BigDecimal getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(BigDecimal horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public BigDecimal getHorasExtras() { return horasExtras; }
    public void setHorasExtras(BigDecimal horasExtras) { this.horasExtras = horasExtras; }
    public BigDecimal getPagoHorasExtras() { return pagoHorasExtras; }
    public void setPagoHorasExtras(BigDecimal pagoHorasExtras) { this.pagoHorasExtras = pagoHorasExtras; }
    public BigDecimal getComisiones() { return comisiones; }
    public void setComisiones(BigDecimal comisiones) { this.comisiones = comisiones; }
    public BigDecimal getBonificaciones() { return bonificaciones; }
    public void setBonificaciones(BigDecimal bonificaciones) { this.bonificaciones = bonificaciones; }
    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }
    public BigDecimal getEssalud() { return essalud; }
    public void setEssalud(BigDecimal essalud) { this.essalud = essalud; }
    public BigDecimal getOnp() { return onp; }
    public void setOnp(BigDecimal onp) { this.onp = onp; }
    public BigDecimal getTotalDeducciones() { return totalDeducciones; }
    public void setTotalDeducciones(BigDecimal totalDeducciones) { this.totalDeducciones = totalDeducciones; }
    public BigDecimal getNetoPagar() { return netoPagar; }
    public void setNetoPagar(BigDecimal netoPagar) { this.netoPagar = netoPagar; }
    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}