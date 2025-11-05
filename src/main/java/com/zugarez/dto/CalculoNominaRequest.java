package com.zugarez.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculoNominaRequest {
    @JsonProperty("empleadoId")
    private Long empleadoId;
    
    @JsonProperty("periodoInicio")
    private LocalDate periodoInicio;
    
    @JsonProperty("periodoFin")
    private LocalDate periodoFin;
    
    private BigDecimal comisiones;
    private BigDecimal bonificaciones;
}
