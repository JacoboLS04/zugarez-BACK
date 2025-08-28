package com.zugarez.zugarez_BACK.CRUD.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;

public class ProductDto {

    @NotBlank(message = "El nombre del producto no puede estar vacío")
    private String name;
    @Min(value = 0, message = "El precio del producto debe ser mayor o igual a 0")
    private double price;
    @NotBlank(message = "Debe ir una url de la imagen")
    private String urlImage;

    public ProductDto() {
    }

    public ProductDto(String name, double price , String urlImage) {
        this.name = name;
        this.price = price;
        this.urlImage = urlImage;

    }

    public String getUrlImage() {
        return urlImage;
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
}


