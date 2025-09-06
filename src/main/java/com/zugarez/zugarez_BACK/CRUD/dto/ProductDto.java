package com.zugarez.zugarez_BACK.CRUD.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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

    @NotBlank(message = "El stock mínimo no puede estar vacío")
    private String stockMinimo;

    public ProductDto() {
    }

    public ProductDto(String name, double price, String brand, Integer supplierId, String description, String urlImage, String stockMinimo) {
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.supplierId = supplierId;
        this.description = description;
        this.urlImage = urlImage;
        this.stockMinimo = stockMinimo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Método para manejar price como String (del frontend)
    public void setPrice(String price) {
        try {
            this.price = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            this.price = 0.0;
        }
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    // Método para manejar supplierId como String (del frontend)
    public void setSupplierId(String supplierId) {
        try {
            this.supplierId = Integer.parseInt(supplierId);
        } catch (NumberFormatException e) {
            this.supplierId = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getStockMinimo() {return stockMinimo;}

    public void setStockMinimo(String stockMinimo) {this.stockMinimo = stockMinimo;}
}


