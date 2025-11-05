package com.zugarez.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    
    @JsonProperty("salarioMinimo")
    private BigDecimal salarioMinimo;
    
    @JsonProperty("salarioMaximo")
    private BigDecimal salarioMaximo;
}
