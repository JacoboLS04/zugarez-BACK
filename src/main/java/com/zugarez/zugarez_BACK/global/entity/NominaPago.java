package com.zugarez.zugarez_BACK.global.entity;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity representing a payroll payment record.
 * Stores salary information including gross salary, deductions, additions, and net payment.
 */
@Entity
@Table(name = "nomina_pago")
public class NominaPago {

    /**
     * Unique identifier for the payment record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pago;

    /**
     * Identifier of the associated contract.
     */
    private Integer id_contrato;

    /**
     * Gross salary amount before deductions.
     */
    private Double salario_bruto;

    /**
     * Total amount of deductions from the gross salary.
     */
    private Double deducciones;

    /**
     * Additional payments or bonuses.
     */
    private Double adiciones;

    /**
     * Net salary after deductions and additions.
     */
    private Double neto;

    /**
     * Date when the payment was made.
     */
    private Date fecha_pago;

    // Getters y setters
}
