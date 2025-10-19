package com.zugarez.zugarez_BACK.global.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "nomina_pago")
public class NominaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pago;
    private Integer id_contrato;
    private Double salario_bruto;
    private Double deducciones;
    private Double adiciones;
    private Double neto;
    private Date fecha_pago;

    // Getters y setters
}
