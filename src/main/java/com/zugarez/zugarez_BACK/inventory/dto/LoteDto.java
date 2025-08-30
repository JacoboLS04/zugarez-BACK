package com.zugarez.zugarez_BACK.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class LoteDto {

    @NotNull(message = "El ID del producto es requerido")
    private Integer productId;

    @NotNull(message = "La fecha de vencimiento es requerida")
    private LocalDate expirationDate;

    @Min(value = 1, message = "La cantidad inicial debe ser mayor a 0")
    private int initialQuantity;

    @Min(value = 0, message = "La cantidad disponible debe ser mayor o igual a 0")
    private int availableQuantity;

    @Min(value = 0, message = "El precio del lote debe ser mayor o igual a 0")
    private double batchPrice;

    @Min(value = 0, message = "El precio por unidad debe ser mayor o igual a 0")
    private double unitPrice;

    public LoteDto() {
    }

    public LoteDto(Integer productId, LocalDate expirationDate, int initialQuantity, 
                   int availableQuantity, double batchPrice, double unitPrice) {
        this.productId = productId;
        this.expirationDate = expirationDate;
        this.initialQuantity = initialQuantity;
        this.availableQuantity = availableQuantity;
        this.batchPrice = batchPrice;
        this.unitPrice = unitPrice;
    }

    // Getters y Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
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
