package com.zugarez.zugarez_BACK.CRUD.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Product.
 * Used for transferring product data between client and server.
 */
public class ProductDto {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;
    
    @Min(value = 0, message = "El precio del producto debe ser mayor o igual a 0")
    private double price;
    
    @NotBlank(message = "La marca no puede estar vacía")
    private String brand;
    
    @NotNull(message = "El ID del proveedor es requerido")
    private Integer supplierId;
    
    private String description;
    
    @NotBlank(message = "Debe ir una url de la imagen")
    private String urlImage;

    @Min(value = 0, message = "El stock mínimo debe ser mayor o igual a 0")
    private int stockMinimo;

    @Min(value = 0, message = "El stock actual debe ser mayor o igual a 0")
    private int stockActual;

    /**
     * Default constructor.
     */
    public ProductDto() {
    }

    /**
     * Full constructor for ProductDto.
     * @param name Product name
     * @param price Product price
     * @param brand Product brand
     * @param supplierId Supplier ID
     * @param description Product description
     * @param urlImage Product image URL
     * @param stockMinimo Minimum stock
     * @param stockActual Current stock
     */
    public ProductDto(String name, double price, String brand, Integer supplierId, String description, String urlImage, int stockMinimo, int stockActual) {
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.supplierId = supplierId;
        this.description = description;
        this.urlImage = urlImage;
        this.stockMinimo = stockMinimo;
        this.stockActual = stockActual;
    }

    /**
     * Gets the product name.
     * @return Product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name.
     * @param name Product name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product price.
     * @return Product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price.
     * @param price Product price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the product price from a String (for frontend compatibility).
     * @param price Product price as String
     */
    public void setPrice(String price) {
        try {
            this.price = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            this.price = 0.0;
        }
    }

    /**
     * Gets the product brand.
     * @return Product brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the product brand.
     * @param brand Product brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Gets the supplier ID.
     * @return Supplier ID
     */
    public Integer getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the supplier ID.
     * @param supplierId Supplier ID
     */
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * Sets the supplier ID from a String (for frontend compatibility).
     * @param supplierId Supplier ID as String
     */
    public void setSupplierId(String supplierId) {
        try {
            this.supplierId = Integer.parseInt(supplierId);
        } catch (NumberFormatException e) {
            this.supplierId = null;
        }
    }

    /**
     * Gets the product description.
     * @return Product description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the product description.
     * @param description Product description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the product image URL.
     * @return Product image URL
     */
    public String getUrlImage() {
        return urlImage;
    }

    /**
     * Sets the product image URL.
     * @param urlImage Product image URL
     */
    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    /**
     * Gets the minimum stock for the product.
     * @return Minimum stock
     */
    public int getStockMinimo() {
        return stockMinimo;
    }

    /**
     * Sets the minimum stock for the product.
     * @param stockMinimo Minimum stock
     */
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    /**
     * Gets the current stock for the product.
     * @return Current stock
     */
    public int getStockActual() {
        return stockActual;
    }

    /**
     * Sets the current stock for the product.
     * @param stockActual Current stock
     */
    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }
}
