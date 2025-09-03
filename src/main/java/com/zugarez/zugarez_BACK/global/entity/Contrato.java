package com.zugarez.zugarez_BACK.global.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "contrato")
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_contrato;
    private Integer id_empleado;
    private Date fecha_inicio;
    private Date fecha_fin;
    private Double salario_base;

    // Getters y setters
}
