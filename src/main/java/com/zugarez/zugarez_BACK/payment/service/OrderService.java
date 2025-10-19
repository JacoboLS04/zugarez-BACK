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
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }
        
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CheckoutRequest.CartItem cartItem : request.getItems()) {
            System.out.println("Procesando producto ID: " + cartItem.productId + ", cantidad: " + cartItem.quantity);
            
            if (cartItem.quantity == null || cartItem.quantity <= 0) {
                throw new RuntimeException("Cantidad inválida para producto ID: " + cartItem.productId);
            }
            
            Optional<Product> productOpt = productRepository.findById(cartItem.productId);
            if (productOpt.isEmpty()) {
                throw new RuntimeException("Producto no encontrado: " + cartItem.productId);
            }

            Product product = productOpt.get();
            
            if (product.getPrice() <= 0.0) {
                throw new RuntimeException("Producto sin precio válido: " + product.getName());
            }
            
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

        BigDecimal tax = subtotal.multiply(BigDecimal.valueOf(0.05));
        BigDecimal total = subtotal.add(tax);
        
        System.out.println("Subtotal: " + subtotal);
        System.out.println("Impuestos (5%): " + tax);
        System.out.println("Total: " + total);

        Order order = new Order();
        order.setUser(user);
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);

        order = orderRepository.save(order);
        System.out.println("Orden guardada con ID: " + order.getId());

        final Order savedOrder = order;
        orderItems.forEach(item -> item.setOrder(savedOrder));
        order.setItems(orderItems);
        order = orderRepository.save(order);
        
        System.out.println("Items asociados a la orden: " + order.getItems().size());

        try {
            Preference preference = mercadoPagoService.createPreference(order);
            order.setMercadopagoPreferenceId(preference.getId());
            order = orderRepository.save(order);
            
            System.out.println("Preferencia de MercadoPago creada: " + preference.getId());
            System.out.println("=== ORDEN COMPLETADA ===");
            
        } catch (Exception e) {
            System.err.println("❌ Error creando preferencia de MercadoPago: " + e.getMessage());
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
        System.out.println("=== OrderService.updateOrderStatus ===");
        System.out.println("Buscando orden con preferenceId: " + preferenceId);
        System.out.println("Nuevo estado: " + status);
        System.out.println("Payment ID: " + paymentId);
        
        try {
            Order order = orderRepository.findByMercadopagoPreferenceId(preferenceId)
                .orElseThrow(() -> {
                    System.err.println("❌ Orden NO encontrada en BD con preferenceId: " + preferenceId);
                    System.err.println("💡 Buscando todas las órdenes recientes para debug...");
                    
                    List<Order> recentOrders = orderRepository.findAll()
                        .stream()
                        .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                        .limit(5)
                        .toList();
                    
                    System.err.println("📋 Últimas 5 órdenes en BD:");
                    recentOrders.forEach(o -> {
                        System.err.println(String.format(
                            "  - ID: %d | PreferenceId: %s | Status: %s",
                            o.getId(),
                            o.getMercadopagoPreferenceId(),
                            o.getStatus()
                        ));
                    });
                    
                    return new RuntimeException("Orden no encontrada con preference ID: " + preferenceId);
                });

            System.out.println("✅ Orden encontrada:");
            System.out.println("  - ID: " + order.getId());
            System.out.println("  - Estado actual: " + order.getStatus());
            System.out.println("  - Usuario: " + order.getUser().getUsername());
            System.out.println("  - Total: " + order.getTotal());

            order.setStatus(status);
            if (paymentId != null && !paymentId.isEmpty()) {
                order.setMercadopagoPaymentId(paymentId);
                System.out.println("✅ Payment ID asignado: " + paymentId);
            }

            order = orderRepository.save(order);
            System.out.println("✅ Orden actualizada en BD exitosamente");
            System.out.println("  - Nuevo estado: " + order.getStatus());
            System.out.println("=====================================");
            
            return order;
            
        } catch (Exception e) {
            System.err.println("❌ Error en updateOrderStatus:");
            System.err.println("Tipo: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
