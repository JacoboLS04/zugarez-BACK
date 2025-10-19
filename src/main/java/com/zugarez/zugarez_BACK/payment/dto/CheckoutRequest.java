package com.zugarez.zugarez_BACK.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CheckoutRequest {
    
    public static class CartItem {
        @NotNull(message = "El ID del producto es obligatorio")
        public Integer productId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        public Integer quantity;
        
        // Getters y Setters
        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    @NotNull(message = "La lista de items es obligatoria")
    private List<CartItem> items;
    
    // Getters y Setters
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
