package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "inventario_movimientos")
public class InventarioMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_movimiento;

    private Integer id_lote;
    private Integer cantidad;
    private String motivo;
    private Date fecha_movimiento;
    private Integer id_tipo_movimiento;

    // Getters y setters
}
