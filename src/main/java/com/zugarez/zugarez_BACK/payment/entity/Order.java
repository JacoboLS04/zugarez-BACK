package com.zugarez.zugarez_BACK.payment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Check;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a customer order in the system.
 * Contains order details, payment information, status tracking, and associated order items.
 */
@Entity
@Table(name = "orders")
@Check(constraints = "status IN ('PENDING', 'APPROVED', 'REJECTED', 'FAILED', 'CANCELLED', 'REFUNDED')")
public class Order {
    
    /**
     * Unique identifier for the order.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    /**
     * User who placed the order.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "loginCode", "verificationToken"})
    private UserEntity user;
    
    /**
     * Total amount including subtotal and tax.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    /**
     * Subtotal amount before tax.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    /**
     * Tax amount applied to the order.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;
    
    /**
     * Current status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    
    /**
     * Payment ID from MercadoPago payment processor.
     */
    @Column(name = "mercadopago_payment_id", length = 100)
    private String mercadopagoPaymentId;
    
    /**
     * Preference ID from MercadoPago for payment setup.
     */
    @Column(name = "mercadopago_preference_id", length = 100)
    private String mercadopagoPreferenceId;
    
    /**
     * Timestamp when the order was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the order was last updated.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * List of items included in the order.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();
    
    /**
     * Sets creation timestamp and default status before persisting.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
    
    /**
     * Updates the timestamp before entity update.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Default constructor.
     */
    public Order() {}
    
    /**
     * Constructs an Order with specified parameters.
     *
     * @param user     the user placing the order
     * @param total    the total amount
     * @param subtotal the subtotal amount
     * @param tax      the tax amount
     * @param status   the order status
     */
    public Order(UserEntity user, BigDecimal total, BigDecimal subtotal, BigDecimal tax, OrderStatus status) {
        this.user = user;
        this.total = total;
        this.subtotal = subtotal;
        this.tax = tax;
        this.status = status;
    }
    
    /**
     * Gets the order ID.
     * @return the order ID
     */
    public Integer getId() { return id; }

    /**
     * Sets the order ID.
     * @param id the order ID
     */
    public void setId(Integer id) { this.id = id; }
    
    /**
     * Gets the user who placed the order.
     * @return the user
     */
    public UserEntity getUser() { return user; }

    /**
     * Sets the user who placed the order.
     * @param user the user
     */
    public void setUser(UserEntity user) { this.user = user; }
    
    /**
     * Gets the total amount.
     * @return the total amount
     */
    public BigDecimal getTotal() { return total; }

    /**
     * Sets the total amount.
     * @param total the total amount
     */
    public void setTotal(BigDecimal total) { this.total = total; }
    
    /**
     * Gets the subtotal amount.
     * @return the subtotal amount
     */
    public BigDecimal getSubtotal() { return subtotal; }

    /**
     * Sets the subtotal amount.
     * @param subtotal the subtotal amount
     */
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    /**
     * Gets the tax amount.
     * @return the tax amount
     */
    public BigDecimal getTax() { return tax; }

    /**
     * Sets the tax amount.
     * @param tax the tax amount
     */
    public void setTax(BigDecimal tax) { this.tax = tax; }
    
    /**
     * Gets the order status.
     * @return the order status
     */
    public OrderStatus getStatus() { return status; }

    /**
     * Sets the order status.
     * @param status the order status
     */
    public void setStatus(OrderStatus status) { this.status = status; }
    
    /**
     * Gets the MercadoPago payment ID.
     * @return the payment ID
     */
    public String getMercadopagoPaymentId() { return mercadopagoPaymentId; }

    /**
     * Sets the MercadoPago payment ID.
     * @param mercadopagoPaymentId the payment ID
     */
    public void setMercadopagoPaymentId(String mercadopagoPaymentId) { this.mercadopagoPaymentId = mercadopagoPaymentId; }
    
    /**
     * Gets the MercadoPago preference ID.
     * @return the preference ID
     */
    public String getMercadopagoPreferenceId() { return mercadopagoPreferenceId; }

    /**
     * Sets the MercadoPago preference ID.
     * @param mercadopagoPreferenceId the preference ID
     */
    public void setMercadopagoPreferenceId(String mercadopagoPreferenceId) { this.mercadopagoPreferenceId = mercadopagoPreferenceId; }
    
    /**
     * Gets the creation timestamp.
     * @return the creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the creation timestamp.
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    /**
     * Gets the last update timestamp.
     * @return the update timestamp
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    /**
     * Sets the last update timestamp.
     * @param updatedAt the update timestamp
     */
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Gets the list of order items.
     * @return the list of order items
     */
    public List<OrderItem> getItems() { return items; }

    /**
     * Sets the list of order items.
     * @param items the list of order items
     */
    public void setItems(List<OrderItem> items) { this.items = items; }
}
