package com.zugarez.zugarez_BACK.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Data Transfer Object for checkout requests.
 * Contains a list of cart items with product IDs and quantities.
 */
public class CheckoutRequest {
    
    /**
     * Represents an item in the shopping cart.
     * Contains product ID and quantity information.
     */
    public static class CartItem {
        /**
         * Product ID (required, must not be null).
         */
        @NotNull(message = "El ID del producto es obligatorio")
        public Integer productId;
        
        /**
         * Quantity of the product (required, must be at least 1).
         */
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        public Integer quantity;
        
        /**
         * Gets the product ID.
         * @return Product ID
         */
        public Integer getProductId() { return productId; }

        /**
         * Sets the product ID.
         * @param productId Product ID
         */
        public void setProductId(Integer productId) { this.productId = productId; }
        
        /**
         * Gets the quantity.
         * @return Quantity
         */
        public Integer getQuantity() { return quantity; }

        /**
         * Sets the quantity.
         * @param quantity Quantity
         */
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
    
    /**
     * List of items in the checkout request (required).
     */
    @NotNull(message = "La lista de items es obligatoria")
    private List<CartItem> items;
    
    /**
     * Gets the list of cart items.
     * @return List of cart items
     */
    public List<CartItem> getItems() { return items; }

    /**
     * Sets the list of cart items.
     * @param items List of cart items
     */
    public void setItems(List<CartItem> items) { this.items = items; }
}
