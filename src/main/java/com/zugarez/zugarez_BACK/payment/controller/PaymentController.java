package com.zugarez.zugarez_BACK.payment.controller;

import com.zugarez.zugarez_BACK.payment.dto.CheckoutRequest;
import com.zugarez.zugarez_BACK.payment.entity.Order;
import com.zugarez.zugarez_BACK.payment.entity.OrderStatus;
import com.zugarez.zugarez_BACK.payment.service.MercadoPagoService;
import com.zugarez.zugarez_BACK.payment.service.OrderService;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MercadoPagoService mercadoPagoService;

    @PostMapping("/checkout")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request, HttpServletRequest httpRequest) {
        System.out.println("=== CHECKPOINT 1: Método checkout llamado ===");
        
        try {
            Object o = httpRequest.getAttribute("authenticatedUser");
            
            if (!(o instanceof UserEntity)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado - Usuario no autenticado"));
            }
            UserEntity user = (UserEntity) o;

            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El carrito está vacío"));
            }

            Order order = orderService.createOrder(user, request);
            
            System.out.println("=== ORDEN CREADA ===");
            System.out.println("Order ID: " + order.getId());

            String checkoutUrl = "https://sandbox.mercadopago.com.co/checkout/v1/redirect?pref_id=" 
                + order.getMercadopagoPreferenceId();

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("preferenceId", order.getMercadopagoPreferenceId());
            response.put("subtotal", order.getSubtotal());
            response.put("tax", order.getTax());
            response.put("total", order.getTotal());
            response.put("publicKey", mercadoPagoService.getPublicKey());
            response.put("checkoutUrl", checkoutUrl);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en checkout: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al procesar el pago: " + e.getMessage()));
        }
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getMyOrders(HttpServletRequest httpRequest) {
        try {
            Object o = httpRequest.getAttribute("authenticatedUser");
            if (!(o instanceof UserEntity)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado"));
            }
            UserEntity user = (UserEntity) o;

            List<Order> orders = orderService.getOrdersByUser(user.getId());
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener pedidos: " + e.getMessage()));
        }
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getOrder(@PathVariable Integer orderId, HttpServletRequest httpRequest) {
        try {
            Object o = httpRequest.getAttribute("authenticatedUser");
            if (!(o instanceof UserEntity)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado"));
            }
            UserEntity user = (UserEntity) o;
            
            Order order = orderService.getOrderById(orderId);
            
            boolean isAdmin = user.getRoles() != null && 
                             user.getRoles().stream().anyMatch(r -> r.name().equals("ROLE_ADMIN"));
            
            if (order.getUser() == null || 
                (!isAdmin && order.getUser().getId() != user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permiso para ver este pedido"));
            }
            
            return ResponseEntity.ok(order);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Pedido no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener el pedido"));
        }
    }

    @GetMapping("/success")
    public void paymentSuccess(
            @RequestParam("collection_id") String collectionId,
            @RequestParam("collection_status") String collectionStatus,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("status") String status,
            @RequestParam("external_reference") String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId,
            HttpServletResponse response) throws IOException {
        
        System.out.println("=== PAGO EXITOSO - CALLBACK RECIBIDO ===");
        
        try {
            OrderStatus newStatus;
            if ("approved".equals(status) || "approved".equals(collectionStatus)) {
                newStatus = OrderStatus.APPROVED;
            } else if ("pending".equals(status)) {
                newStatus = OrderStatus.PENDING;
            } else {
                newStatus = OrderStatus.FAILED;
            }
            
            Order order = orderService.updateOrderStatus(preferenceId, newStatus, paymentId);
            System.out.println("✅ Orden actualizada: " + order.getId());
            
            String redirectUrl = String.format(
                "https://zugarez.vercel.app/payment/success?orderId=%d&status=%s&paymentId=%s&total=%s",
                order.getId(),
                newStatus.toString(),
                paymentId,
                order.getTotal()
            );
            
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            response.sendRedirect("https://zugarez.vercel.app/payment/error");
        }
    }

    @GetMapping("/failure")
    public void paymentFailure(
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId,
            HttpServletResponse response) throws IOException {
        
        System.out.println("=== PAGO FALLIDO ===");
        
        try {
            if (preferenceId != null) {
                Order order = orderService.updateOrderStatus(preferenceId, OrderStatus.FAILED, null);
                
                String redirectUrl = String.format(
                    "https://zugarez.vercel.app/payment/failure?orderId=%d",
                    order.getId()
                );
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect("https://zugarez.vercel.app/payment/failure");
            }
            
        } catch (Exception e) {
            response.sendRedirect("https://zugarez.vercel.app/payment/error");
        }
    }

    @GetMapping("/pending")
    public void paymentPending(
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId,
            HttpServletResponse response) throws IOException {
        
        System.out.println("=== PAGO PENDIENTE ===");
        
        try {
            if (preferenceId != null) {
                Order order = orderService.updateOrderStatus(preferenceId, OrderStatus.PENDING, null);
                
                String redirectUrl = String.format(
                    "https://zugarez.vercel.app/payment/pending?orderId=%d",
                    order.getId()
                );
                response.sendRedirect(redirectUrl);
            } else {
                response.sendRedirect("https://zugarez.vercel.app/payment/pending");
            }
            
        } catch (Exception e) {
            response.sendRedirect("https://zugarez.vercel.app/payment/error");
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload) {
        System.out.println("=== WEBHOOK MERCADOPAGO ===");

        try {
            String type = (String) payload.get("type");
            
            if ("payment".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String paymentId = (String) data.get("id");
                System.out.println("Payment ID: " + paymentId);
                
                return ResponseEntity.ok(Map.of("message", "Webhook procesado"));
            }

            return ResponseEntity.ok(Map.of("message", "Evento no manejado"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error procesando webhook"));
        }
    }

    @GetMapping("/status/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable Integer orderId, HttpServletRequest httpRequest) {
        try {
            Object o = httpRequest.getAttribute("authenticatedUser");
            if (!(o instanceof UserEntity)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado"));
            }
            UserEntity user = (UserEntity) o;
            
            Order order = orderService.getOrderById(orderId);
            
            boolean isAdmin = user.getRoles() != null && 
                             user.getRoles().stream().anyMatch(r -> r.name().equals("ROLE_ADMIN"));
            
            if (order.getUser() == null || 
                (!isAdmin && order.getUser().getId() != user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permiso para ver este pedido"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("status", order.getStatus());
            response.put("subtotal", order.getSubtotal());
            response.put("tax", order.getTax());
            response.put("total", order.getTotal());
            response.put("preferenceId", order.getMercadopagoPreferenceId());
            response.put("paymentId", order.getMercadopagoPaymentId());
            response.put("createdAt", order.getCreatedAt());
            response.put("updatedAt", order.getUpdatedAt());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Pedido no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al verificar estado del pago"));
        }
    }
}