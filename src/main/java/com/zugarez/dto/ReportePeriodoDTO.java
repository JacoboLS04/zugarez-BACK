package com.zugarez.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportePeriodoDTO {
    @JsonProperty("periodoInicio")
    private LocalDate periodoInicio;
    
    @JsonProperty("periodoFin")
    private LocalDate periodoFin;
    
    @JsonProperty("totalEmpleados")
    private Long totalEmpleados;
    
    @JsonProperty("totalSalarios")
    private BigDecimal totalSalarios;
    
    @JsonProperty("totalDeducciones")
    private BigDecimal totalDeducciones;
    
    @JsonProperty("totalHorasExtras")
    private Double totalHorasExtras;
    
    @JsonProperty("estadisticasPorEstado")
    private Map<String, Long> estadisticasPorEstado;
}
