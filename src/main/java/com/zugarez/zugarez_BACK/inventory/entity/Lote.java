package com.zugarez.zugarez_BACK.inventory.entity;

import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity representing an inventory batch (lote) of products.
 * Tracks product quantities, expiration dates, and pricing information.
 */
@Entity
@Table(name = "lotes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lote {

    /**
     * Unique identifier for the batch.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Product associated with this batch.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    /**
     * Expiration date of the batch.
     */
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    /**
     * Initial quantity when the batch was created.
     */
    @Column(name = "initial_quantity", nullable = false)
    private int initialQuantity;

    /**
     * Current available quantity in the batch.
     */
    @Column(name = "available_quantity", nullable = false)
    private int availableQuantity;

    /**
     * Total price paid for the batch.
     */
    @Column(name = "batch_price", nullable = false)
    private double batchPrice;

    /**
     * Price per unit in the batch.
     */
    @Column(name = "unit_price", nullable = false)
    private double unitPrice;

    /**
     * Date when the batch was created.
     */
    @Column(name = "created_at")
    private LocalDate createdAt;

    /**
     * Default constructor. Sets creation date to current date.
     */
    public Lote() {
        this.createdAt = LocalDate.now();
    }

    /**
     * Full constructor for Lote.
     * @param product the product
     * @param expirationDate the expiration date
     * @param initialQuantity the initial quantity
     * @param availableQuantity the available quantity
     * @param batchPrice the batch price
     * @param unitPrice the unit price
     */
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

    /**
     * Gets the batch ID.
     * @return the batch ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the batch ID.
     * @param id the batch ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the product.
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product.
     * @param product the product
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the expiration date.
     * @return the expiration date
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiration date.
     * @param expirationDate the expiration date
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the initial quantity.
     * @return the initial quantity
     */
    public int getInitialQuantity() {
        return initialQuantity;
    }

    /**
     * Sets the initial quantity.
     * @param initialQuantity the initial quantity
     */
    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    /**
     * Gets the available quantity.
     * @return the available quantity
     */
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Sets the available quantity.
     * @param availableQuantity the available quantity
     */
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * Gets the batch price.
     * @return the batch price
     */
    public double getBatchPrice() {
        return batchPrice;
    }

    /**
     * Sets the batch price.
     * @param batchPrice the batch price
     */
    public void setBatchPrice(double batchPrice) {
        this.batchPrice = batchPrice;
    }

    /**
     * Gets the unit price.
     * @return the unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unit price.
     * @param unitPrice the unit price
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Gets the creation date.
     * @return the creation date
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation date.
     * @param createdAt the creation date
     */
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
