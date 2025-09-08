package com.zugarez.zugarez_BACK.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Data Transfer Object for batch (lote) operations in inventory.
 * Contains product ID, expiration date, quantities, and pricing information.
 */
public class LoteDto {
    /** Product ID associated with the batch (required) */
    @NotNull(message = "El ID del producto es requerido")
    private Integer productId;
    /** Expiration date of the batch (required) */
    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDate expirationDate;
    /** Initial quantity in the batch (must be > 0) */
    @Min(value = 1, message = "La cantidad inicial debe ser mayor a 0")
    private int initialQuantity;
    /** Available quantity in the batch (must be >= 0) */
    @Min(value = 0, message = "La cantidad disponible debe ser mayor o igual a 0")
    private int availableQuantity;
    /** Price of the entire batch (must be >= 0) */
    @Min(value = 0, message = "El precio del lote debe ser mayor o igual a 0")
    private double batchPrice;
    /** Price per unit in the batch (must be >= 0) */
    @Min(value = 0, message = "El precio por unidad debe ser mayor o igual a 0")
    private double unitPrice;

    /**
     * Default constructor.
     */
    public LoteDto() {
    }

    /**
     * Full constructor for LoteDto.
     * @param productId Product ID
     * @param expirationDate Expiration date
     * @param initialQuantity Initial quantity
     * @param availableQuantity Available quantity
     * @param batchPrice Batch price
     * @param unitPrice Unit price
     */
    public LoteDto(Integer productId, LocalDate expirationDate, int initialQuantity,
                   int availableQuantity, double batchPrice, double unitPrice) {
        this.productId = productId;
        this.expirationDate = expirationDate;
        this.initialQuantity = initialQuantity;
        this.availableQuantity = availableQuantity;
        this.batchPrice = batchPrice;
        this.unitPrice = unitPrice;
    }

    /**
     * Gets the product ID.
     * @return Product ID
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     * @param productId Product ID
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * Gets the expiration date.
     * @return Expiration date
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expiration date.
     * @param expirationDate Expiration date
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the initial quantity.
     * @return Initial quantity
     */
    public int getInitialQuantity() {
        return initialQuantity;
    }

    /**
     * Sets the initial quantity.
     * @param initialQuantity Initial quantity
     */
    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    /**
     * Gets the available quantity.
     * @return Available quantity
     */
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    /**
     * Sets the available quantity.
     * @param availableQuantity Available quantity
     */
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * Gets the batch price.
     * @return Batch price
     */
    public double getBatchPrice() {
        return batchPrice;
    }

    /**
     * Sets the batch price.
     * @param batchPrice Batch price
     */
    public void setBatchPrice(double batchPrice) {
        this.batchPrice = batchPrice;
    }

    /**
     * Gets the unit price.
     * @return Unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unit price.
     * @param unitPrice Unit price
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Métodos para manejar strings del frontend
    public void setProductId(String productId) {
        try {
            this.productId = Integer.parseInt(productId);
        } catch (NumberFormatException e) {
            this.productId = null;
        }
    }

    public void setInitialQuantity(String initialQuantity) {
        try {
            this.initialQuantity = Integer.parseInt(initialQuantity);
        } catch (NumberFormatException e) {
            this.initialQuantity = 0;
        }
    }

    public void setAvailableQuantity(String availableQuantity) {
        try {
            this.availableQuantity = Integer.parseInt(availableQuantity);
        } catch (NumberFormatException e) {
            this.availableQuantity = 0;
        }
    }

    public void setBatchPrice(String batchPrice) {
        try {
            this.batchPrice = Double.parseDouble(batchPrice);
        } catch (NumberFormatException e) {
            this.batchPrice = 0.0;
        }
    }

    public void setUnitPrice(String unitPrice) {
        try {
            this.unitPrice = Double.parseDouble(unitPrice);
        } catch (NumberFormatException e) {
            this.unitPrice = 0.0;
        }
    }
}
