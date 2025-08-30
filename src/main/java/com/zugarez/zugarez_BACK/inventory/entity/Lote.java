package com.zugarez.zugarez_BACK.inventory.entity;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "lotes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "initial_quantity", nullable = false)
    private int initialQuantity;

    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    @Column(name = "batch_price", nullable = false)
    private double batchPrice;

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    @Column(name = "created_at")
    private LocalDate createdAt;

    public Lote() {
        this.createdAt = LocalDate.now();
    }

    public Lote(Product product, LocalDate expirationDate, int initialQuantity, 
                int availableQuantity, double batchPrice, double unitPrice) {
        this.product = product;
        this.expirationDate = expirationDate;
        this.initialQuantity = initialQuantity;
        this.availableQuantity = availableQuantity;
        this.batchPrice = batchPrice;
        this.unitPrice = unitPrice;
        this.createdAt = LocalDate.now();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public double getBatchPrice() {
        return batchPrice;
    }

    public void setBatchPrice(double batchPrice) {
        this.batchPrice = batchPrice;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
