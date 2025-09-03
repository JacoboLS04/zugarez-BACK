package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pedido;

    private Integer id_cliente;
    private Date fecha_pedido;
    private String id_estado_pedido;
    private Double total_pedido;

    // Getters y setters
}
