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
        
        System.out.println("===========================================");
        System.out.println("=== PAGO EXITOSO - CALLBACK RECIBIDO ===");
        System.out.println("===========================================");
        System.out.println("📋 Parámetros recibidos:");
        System.out.println("  - Collection ID: " + collectionId);
        System.out.println("  - Collection Status: " + collectionStatus);
        System.out.println("  - Payment ID: " + paymentId);
        System.out.println("  - Status: " + status);
        System.out.println("  - External Reference: " + externalReference);
        System.out.println("  - Preference ID: " + preferenceId);
        System.out.println("===========================================");
        
        try {
            // Validación de parámetros
            if (preferenceId == null || preferenceId.isEmpty()) {
                System.err.println("❌ ERROR: preferenceId es NULL o vacío");
                throw new RuntimeException("preferenceId no proporcionado");
            }
            
            System.out.println("✅ Step 1: Parámetros validados correctamente");
            
            // Determinar el estado correcto
            OrderStatus newStatus;
            if ("approved".equals(status) || "approved".equals(collectionStatus)) {
                newStatus = OrderStatus.APPROVED;
                System.out.println("✅ Step 2: Estado determinado = APPROVED");
            } else if ("pending".equals(status)) {
                newStatus = OrderStatus.PENDING;
                System.out.println("⏳ Step 2: Estado determinado = PENDING");
            } else {
                newStatus = OrderStatus.FAILED;
                System.out.println("❌ Step 2: Estado determinado = FAILED");
            }
            
            System.out.println("🔄 Step 3: Intentando actualizar orden en BD...");
            System.out.println("   - Buscando orden por preferenceId: " + preferenceId);
            
            // Actualizar orden
            Order order = orderService.updateOrderStatus(preferenceId, newStatus, paymentId);
            
            System.out.println("✅ Step 4: Orden actualizada exitosamente");
            System.out.println("   - Order ID: " + order.getId());
            System.out.println("   - Nuevo Estado: " + order.getStatus());
            System.out.println("   - Payment ID guardado: " + order.getMercadopagoPaymentId());
            
            // Construir URL de redirección
            String redirectUrl = String.format(
                "https://zugarez.vercel.app/products?paymentSuccess=true&orderId=%d&status=%s&total=%s",
                order.getId(),
                newStatus.toString(),
                order.getTotal()
            );
            
            System.out.println("✅ Step 5: URL de redirección construida");
            System.out.println("   - URL: " + redirectUrl);
            System.out.println("🔄 Redirigiendo al frontend...");
            System.out.println("===========================================");
            
            response.sendRedirect(redirectUrl);
            
        } catch (Exception e) {
            System.err.println("===========================================");
            System.err.println("❌❌❌ ERROR CRÍTICO EN CALLBACK ❌❌❌");
            System.err.println("===========================================");
            System.err.println("Tipo de error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Stack trace:");
            e.printStackTrace();
            System.err.println("===========================================");
            System.err.println("Parámetros que causaron el error:");
            System.err.println("  - preferenceId: " + preferenceId);
            System.err.println("  - paymentId: " + paymentId);
            System.err.println("  - status: " + status);
            System.err.println("===========================================");
            
            // Redirigir con información del error
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            String redirectUrl = String.format(
                "https://zugarez.vercel.app/products?paymentError=true&errorMsg=%s&preferenceId=%s&paymentId=%s",
                errorMsg.replace(" ", "_"),
                preferenceId != null ? preferenceId : "null",
                paymentId
            );
            
            System.err.println("🔄 Redirigiendo con error: " + redirectUrl);
            response.sendRedirect(redirectUrl);
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
                System.out.println("❌ Orden FALLIDA: " + order.getId());
            }
            
            response.sendRedirect("https://zugarez.vercel.app/products?paymentFailed=true");
            
        } catch (Exception e) {
            response.sendRedirect("https://zugarez.vercel.app/products?paymentError=true");
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
                System.out.println("⏳ Orden PENDIENTE: " + order.getId());
            }
            
            response.sendRedirect("https://zugarez.vercel.app/products?paymentPending=true");
            
        } catch (Exception e) {
            response.sendRedirect("https://zugarez.vercel.app/products?paymentError=true");
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