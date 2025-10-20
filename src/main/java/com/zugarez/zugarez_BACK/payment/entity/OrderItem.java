package com.zugarez.zugarez_BACK.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entity representing an item within an order.
 * Links products to orders with quantity and pricing information.
 */
@Entity
@Table(name = "order_items")
public class OrderItem {
    
    /**
     * Unique identifier for the order item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * Reference to the parent order containing this item.
     */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties("items")
    private Order order;
    
    /**
     * Product associated with this order item.
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"supplier"})
    private Product product;
    
    /**
     * Quantity of the product ordered.
     */
    @Column(nullable = false)
    private Integer quantity;
    
    /**
     * Price per unit of the product at the time of order.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Subtotal amount (price × quantity).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    /**
     * Default constructor.
     */
    public OrderItem() {}
    
    /**
     * Constructs an OrderItem with specified parameters.
     * Automatically calculates subtotal based on price and quantity.
     *
     * @param order    the parent order
     * @param product  the product being ordered
     * @param quantity the quantity ordered
     * @param price    the price per unit
     */
    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }
    
    /**
     * Gets the order item ID.
     * @return the order item ID
     */
    public Integer getId() { return id; }

    /**
     * Sets the order item ID.
     * @param id the order item ID
     */
    public void setId(Integer id) { this.id = id; }
    
    /**
     * Gets the parent order.
     * @return the parent order
     */
    public Order getOrder() { return order; }

    /**
     * Sets the parent order.
     * @param order the parent order
     */
    public void setOrder(Order order) { this.order = order; }
    
    /**
     * Gets the product.
     * @return the product
     */
    public Product getProduct() { return product; }

    /**
     * Sets the product.
     * @param product the product
     */
    public void setProduct(Product product) { this.product = product; }
    
    /**
     * Gets the quantity ordered.
     * @return the quantity
     */
    public Integer getQuantity() { return quantity; }

    /**
     * Sets the quantity and recalculates subtotal.
     * @param quantity the quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        if (this.price != null && quantity != null) {
            this.subtotal = this.price.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    /**
     * Gets the price per unit.
     * @return the price
     */
    public BigDecimal getPrice() { return price; }

    /**
     * Sets the price and recalculates subtotal.
     * @param price the price per unit
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
        if (this.quantity != null && price != null) {
            this.subtotal = price.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
    
    /**
     * Gets the subtotal amount.
     * @return the subtotal
     */
    public BigDecimal getSubtotal() { return subtotal; }

    /**
     * Sets the subtotal amount.
     * @param subtotal the subtotal
     */
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
