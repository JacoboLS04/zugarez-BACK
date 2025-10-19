package com.zugarez.zugarez_BACK.payment.service;

import com.mercadopago.resources.preference.Preference;
import com.zugarez.zugarez_BACK.CRUD.entity.Product;
import com.zugarez.zugarez_BACK.CRUD.repository.ProductRepository;
import com.zugarez.zugarez_BACK.payment.dto.CheckoutRequest;
import com.zugarez.zugarez_BACK.payment.entity.Order;
import com.zugarez.zugarez_BACK.payment.entity.OrderItem;
import com.zugarez.zugarez_BACK.payment.entity.OrderStatus;
import com.zugarez.zugarez_BACK.payment.repository.OrderRepository;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @Transactional
    public Order createOrder(UserEntity user, CheckoutRequest request) {
        System.out.println("=== CREANDO ORDEN ===");
        System.out.println("Usuario: " + user.getUsername() + " (ID: " + user.getId() + ")");
        System.out.println("Items: " + request.getItems().size());
        
        // Validar que haya items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CheckoutRequest.CartItem cartItem : request.getItems()) {
            System.out.println("Procesando producto ID: " + cartItem.productId + ", cantidad: " + cartItem.quantity);
            
            // Validar cantidad
            if (cartItem.quantity == null || cartItem.quantity <= 0) {
                throw new RuntimeException("Cantidad inválida para producto ID: " + cartItem.productId);
            }
            
            Optional<Product> productOpt = productRepository.findById(cartItem.productId);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Producto no encontrado: " + cartItem.productId);
            }

            Product product = productOpt.get();
            
            // Validar que el producto tenga precio (comparar con 0.0 para Double)
            if (product.getPrice() == null || product.getPrice() <= 0.0) { // ✅ Comparar con 0.0
                throw new RuntimeException("Producto sin precio válido: " + product.getName());
            }
            
            // Convertir Double a BigDecimal para cálculos
            BigDecimal price = BigDecimal.valueOf(product.getPrice());
            BigDecimal itemSubtotal = price.multiply(BigDecimal.valueOf(cartItem.quantity));
            subtotal = subtotal.add(itemSubtotal);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.quantity);
            orderItem.setPrice(price);
            orderItem.setSubtotal(itemSubtotal);
            orderItems.add(orderItem);
            
            System.out.println("Producto: " + product.getName() + " - Precio: " + price + " - Subtotal: " + itemSubtotal);
        }

        // Calcular impuestos (5% IVA)
        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05));
        BigDecimal total = subtotal.add(tax);
        
        System.out.println("Subtotal: " + subtotal);
        System.out.println("Impuestos (5%): " + tax);
        System.out.println("Total: " + total);

        // Crear orden
        Order order = new Order();
        order.setUser(user);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);

        // Guardar orden primero
        order = orderRepository.save(order);
        System.out.println("Orden guardada con ID: " + order.getId());

        // Asociar items a la orden y guardar
        final Order savedOrder = order;
        orderItems.forEach(item -> item.setOrder(savedOrder));
        order.setItems(orderItems);
        order = orderRepository.save(order);
        
        System.out.println("Items asociados a la orden: " + order.getItems().size());

        try {
            // Crear preferencia de MercadoPago
            Preference preference = mercadoPagoService.createPreference(order);
            order.setMercadopagoPreferenceId(preference.getId());
            order = orderRepository.save(order);
            
            System.out.println("Preferencia de MercadoPago creada: " + preference.getId());
            System.out.println("=== ORDEN COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error creando preferencia de MercadoPago: " + e.getMessage());
            // Marcar orden como fallida
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new RuntimeException("Error al crear preferencia de pago: " + e.getMessage(), e);
        }

        return order;
    }

    public Order getOrderById(Integer orderId) {
        System.out.println("Buscando orden ID: " + orderId);
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + orderId));
    }

    public List<Order> getOrdersByUser(Integer userId) {
        System.out.println("Obteniendo órdenes del usuario ID: " + userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        System.out.println("Órdenes encontradas: " + orders.size());
        return orders;
    }

    @Transactional
    public Order updateOrderStatus(String preferenceId, OrderStatus status, String paymentId) {
        System.out.println("=== ACTUALIZANDO ESTADO DE ORDEN ===");
        System.out.println("Preference ID: " + preferenceId);
        System.out.println("Nuevo estado: " + status);
        System.out.println("Payment ID: " + paymentId);
        
        Order order = orderRepository.findByMercadopagoPreferenceId(preferenceId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con preference ID: " + preferenceId));

        order.setStatus(status);
        if (paymentId != null && !paymentId.isEmpty()) {
            order.setMercadopagoPaymentId(paymentId);
        }

        order = orderRepository.save(order);
        System.out.println("Estado actualizado para orden ID: " + order.getId());
        
        return order;
    }
}
