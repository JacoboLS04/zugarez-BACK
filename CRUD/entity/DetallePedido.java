package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_detalle;

    private Integer id_pedido;
    private Integer id_lote;
    private Integer cantidad;
    private Double subtotal;

    // Getters y setters
}
