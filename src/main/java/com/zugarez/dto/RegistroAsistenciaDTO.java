package com.zugarez.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zugarez.model.TipoTurno;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAsistenciaDTO {
    private Long id;
    
    @JsonProperty("empleadoId")
    private Long empleadoId;
    
    @JsonProperty("empleadoNombre")
    private String empleadoNombre;
    
    private LocalDate fecha;
    
    @JsonProperty("horaEntrada")
    private LocalDateTime horaEntrada;
    
    @JsonProperty("horaSalida")
    private LocalDateTime horaSalida;
    
    private TipoTurno turno;
    
    @JsonProperty("horasTrabajadas")
    private Double horasTrabajadas;
    
    @JsonProperty("horasExtras")
    private Double horasExtras;
    
    private String observaciones;
}
