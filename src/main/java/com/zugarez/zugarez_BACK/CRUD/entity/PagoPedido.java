package com.zugarez.zugarez_BACK.CRUD.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pago_pedido")
public class PagoPedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_pago;
    private Integer id_pedido;
    private Double monto;
    private Integer id_metodo_pago;

    // Getters y setters
}
