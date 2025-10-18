package com.zugarez.zugarez_BACK.CRUD.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Entity representing a product in the system.
 * Contains product details such as name, price, brand, supplier, description, image URL, and stock information.
 */
@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "price", nullable = false)
    private double price;
    
    @Column(name = "brand")
    private String brand;

    @Column(name = "supplier_id")
    private int supplierId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "url_image")
    private String urlImage;

    @Column(name = "stock_minimo", nullable = false)
    private int stockMinimo;

    @Column(name = "stock_actual", nullable = false)
    private int stockActual;

    /**
     * Default constructor.
     */
    public Product() {
    }

    /**
     * Full constructor for Product.
     * @param id Product ID
     * @param name Product name
     * @param price Product price
     * @param brand Product brand
     * @param supplierId Supplier ID
     * @param description Product description
     * @param urlImage URL of the product image
     * @param stockMinimo Minimum stock
     * @param stockActual Current stock
     */
    public Product(int id, String name, double price, String brand, int supplierId, String description, String urlImage, int stockMinimo, int stockActual) {
        this.id = id;
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
     * Gets the product ID.
     * @return Product ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the product ID.
     * @param id Product ID
     */
    public void setId(int id) {
        this.id = id;
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
    public int getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the supplier ID.
     * @param supplierId Supplier ID
     */
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
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
