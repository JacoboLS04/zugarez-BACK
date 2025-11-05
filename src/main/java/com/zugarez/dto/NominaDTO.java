package com.zugarez.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zugarez.model.EstadoNomina;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NominaDTO {
    private Long id;
    
    @JsonProperty("empleadoId")
    private Long empleadoId;
    
    @JsonProperty("empleadoNombre")
    private String empleadoNombre;
    
    @JsonProperty("periodoInicio")
    private LocalDate periodoInicio;
    
    @JsonProperty("periodoFin")
    private LocalDate periodoFin;
    
    @JsonProperty("salarioBase")
    private BigDecimal salarioBase;
    
    @JsonProperty("horasTrabajadas")
    private Double horasTrabajadas;
    
    @JsonProperty("horasExtras")
    private Double horasExtras;
    
    @JsonProperty("pagoHorasExtras")
    private BigDecimal pagoHorasExtras;
    
    private BigDecimal comisiones;
    private BigDecimal bonificaciones;
    
    @JsonProperty("totalIngresos")
    private BigDecimal totalIngresos;
    
    @JsonProperty("deduccionEssalud")
    private BigDecimal deduccionEssalud;
    
    @JsonProperty("deduccionOnp")
    private BigDecimal deduccionOnp;
    
    @JsonProperty("totalDeducciones")
    private BigDecimal totalDeducciones;
    
    @JsonProperty("salarioNeto")
    private BigDecimal salarioNeto;
    
    private EstadoNomina estado;
    
    @JsonProperty("fechaPago")
    private LocalDate fechaPago;
    
    @JsonProperty("numeroTransaccion")
    private String numeroTransaccion;
}
