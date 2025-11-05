package com.zugarez.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zugarez.model.TipoContrato;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private String telefono;
    
    @JsonProperty("fechaContratacion")
    private LocalDate fechaContratacion;
    
    @JsonProperty("salarioBase")
    private BigDecimal salarioBase;
    
    @JsonProperty("tipoContrato")
    private TipoContrato tipoContrato;
    
    @JsonProperty("cuentaBancaria")
    private String cuentaBancaria;
    
    private String banco;
    private Boolean activo;
    
    @JsonProperty("puestoId")
    private Long puestoId;
    
    @JsonProperty("puestoNombre")
    private String puestoNombre;
}
