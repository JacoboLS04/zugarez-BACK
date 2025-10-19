package com.zugarez.zugarez_BACK.payment.controller;

import com.zugarez.zugarez_BACK.payment.dto.CheckoutRequest;
import com.zugarez.zugarez_BACK.payment.entity.Order;
import com.zugarez.zugarez_BACK.payment.entity.OrderStatus;
import com.zugarez.zugarez_BACK.payment.service.MercadoPagoService;
import com.zugarez.zugarez_BACK.payment.service.OrderService;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("Request URI: " + httpRequest.getRequestURI());
        System.out.println("Authorization header: " + httpRequest.getHeader("Authorization"));
        
        try {
            Object o = httpRequest.getAttribute("authenticatedUser");
            System.out.println("=== CHECKPOINT 2: Obteniendo usuario autenticado ===");
            System.out.println("authenticatedUser attribute: " + (o != null ? o.getClass().getName() : "null"));
            
            if (!(o instanceof UserEntity)) {
                System.err.println("❌ ERROR: Usuario no autenticado o atributo incorrecto");
                System.err.println("Tipo de objeto: " + (o != null ? o.getClass().getName() : "null"));
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado - Usuario no autenticado"));
            }
            UserEntity user = (UserEntity) o;
            
            System.out.println("✅ Usuario autenticado: " + user.getUsername() + " (ID: " + user.getId() + ")");
            System.out.println("Roles: " + user.getRoles());
            System.out.println("Items en el carrito: " + request.getItems().size());

            if (request.getItems() == null || request.getItems().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "El carrito está vacío"));
            }

            Order order = orderService.createOrder(user, request);
            
            System.out.println("=== ORDEN CREADA ===");
            System.out.println("Order ID: " + order.getId());
            System.out.println("Subtotal: " + order.getSubtotal());
            System.out.println("Tax: " + order.getTax());
            System.out.println("Total: " + order.getTotal());
            System.out.println("Preference ID: " + order.getMercadopagoPreferenceId());

            // Usar sandbox URL para credenciales de prueba
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

            System.out.println("✅ Checkout URL: " + checkoutUrl);
            System.out.println("");
            System.out.println("🎯 INSTRUCCIONES FINALES - MODO SANDBOX:");
            System.out.println("════════════════════════════════════════════════");
            System.out.println("1. Abre la URL en NAVEGADOR INCÓGNITO (Ctrl+Shift+N)");
            System.out.println("2. Cuando MercadoPago pida iniciar sesión, usa:");
            System.out.println("   👤 Usuario: TESTUSER7191328507680256966");
            System.out.println("   🔑 Contraseña: p4mhJvbM7Z");
            System.out.println("");
            System.out.println("3. Paga con tarjeta de prueba:");
            System.out.println("   💳 Número: 5031 7557 3453 0604");
            System.out.println("   👤 Nombre: APRO (para aprobar automáticamente)");
            System.out.println("   🔒 CVV: 123");
            System.out.println("   📅 Vencimiento: 11/25");
            System.out.println("");
            System.out.println("✅ Ahora las credenciales están CORRECTAMENTE vinculadas");
            System.out.println("✅ Vendedor: zugarez_vendedor (2932784465)");
            System.out.println("✅ Comprador: comprador_zugarez_2 (2932784463)");
            System.out.println("════════════════════════════════════════════════");
            System.out.println("=== CHECKOUT COMPLETADO ===");
            
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
            System.out.println("Pedidos encontrados para usuario " + user.getId() + ": " + orders.size());
            
            return ResponseEntity.ok(orders);
            
        } catch (Exception e) {
            System.out.println("ERROR obteniendo pedidos: " + e.getMessage());
            e.printStackTrace();
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
            
            // Verificar que el pedido pertenezca al usuario (o sea admin)
            // Usar comparación de primitivos y comprobar nulidad del usuario del pedido
            if (order.getUser() == null ||
                (order.getUser().getId() != user.getId() &&
                 (user.getRoles() == null || user.getRoles().stream().noneMatch(r -> r.name().equals("ROLE_ADMIN"))))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "No tienes permiso para ver este pedido"));
            }
            
            return ResponseEntity.ok(order);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Pedido no encontrado"));
        } catch (Exception e) {
            System.out.println("ERROR obteniendo pedido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener el pedido"));
        }
    }

    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(
            @RequestParam("collection_id") String collectionId,
            @RequestParam("collection_status") String collectionStatus,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("status") String status,
            @RequestParam("external_reference") String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {
        
        System.out.println("=== PAGO EXITOSO - CALLBACK RECIBIDO ===");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Status: " + status);
        System.out.println("Collection Status: " + collectionStatus);
        System.out.println("External Reference (Order ID): " + externalReference);
        System.out.println("Preference ID: " + preferenceId);
        
        try {
            Integer orderId = Integer.parseInt(externalReference);
            
            // Actualizar estado de la orden
            OrderStatus newStatus;
            if ("approved".equals(status) || "approved".equals(collectionStatus)) {
                newStatus = OrderStatus.APPROVED;
            } else if ("pending".equals(status)) {
                newStatus = OrderStatus.PENDING;
            } else {
                newStatus = OrderStatus.FAILED;
            }
            
            Order order = orderService.updateOrderStatus(preferenceId, newStatus, paymentId);
            
            System.out.println("✅ Orden actualizada: " + order.getId() + " - Estado: " + newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", order.getId());
            response.put("status", order.getStatus());
            response.put("paymentId", paymentId);
            response.put("total", order.getTotal());
            response.put("message", "Pago procesado exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando callback de éxito: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error procesando el pago: " + e.getMessage()));
        }
    }

    @GetMapping("/failure")
    public ResponseEntity<?> paymentFailure(
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {
        
        System.out.println("=== PAGO FALLIDO - CALLBACK RECIBIDO ===");
        System.out.println("External Reference (Order ID): " + externalReference);
        System.out.println("Preference ID: " + preferenceId);
        
        try {
            if (preferenceId != null) {
                Order order = orderService.updateOrderStatus(preferenceId, OrderStatus.FAILED, null);
                System.out.println("❌ Orden marcada como fallida: " + order.getId());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "El pago fue rechazado o cancelado");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando callback de fallo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error procesando el fallo"));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> paymentPending(
            @RequestParam(value = "external_reference", required = false) String externalReference,
            @RequestParam(value = "preference_id", required = false) String preferenceId) {
        
        System.out.println("=== PAGO PENDIENTE - CALLBACK RECIBIDO ===");
        System.out.println("External Reference (Order ID): " + externalReference);
        
        try {
            if (preferenceId != null) {
                Order order = orderService.updateOrderStatus(preferenceId, OrderStatus.PENDING, null);
                System.out.println("⏳ Orden marcada como pendiente: " + order.getId());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "El pago está pendiente de confirmación");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando callback pendiente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error procesando el estado pendiente"));
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody Map<String, Object> payload) {
        System.out.println("=== WEBHOOK MERCADOPAGO ===");
        System.out.println("Payload: " + payload);

        try {
            String type = (String) payload.get("type");
            
            if ("payment".equals(type)) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String paymentId = (String) data.get("id");
                
                System.out.println("Payment ID recibido: " + paymentId);
                
                return ResponseEntity.ok(Map.of("message", "Webhook procesado"));
            }

            return ResponseEntity.ok(Map.of("message", "Evento no manejado"));

        } catch (Exception e) {
            System.err.println("❌ Error procesando webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error procesando webhook"));
        }
    }

    @GetMapping("/status/{orderId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable Integer orderId, HttpServletRequest httpRequest) {
        System.out.println("=== VERIFICANDO ESTADO DE PAGO ===");
        System.out.println("Order ID: " + orderId);
        
        try {
            Object o = httpRequest.getAttribute("authenticatedUser");
            if (!(o instanceof UserEntity)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autorizado"));
            }
            UserEntity user = (UserEntity) o;
            
            Order order = orderService.getOrderById(orderId);
            
            // Verificar propiedad (permitir a admins ver cualquier orden)
            boolean isAdmin = user.getRoles() != null && 
                             user.getRoles().stream().anyMatch(r -> r.name().equals("ROLE_ADMIN"));
            
            if (order.getUser() == null || 
                (!isAdmin && order.getUser().getId() != user.getId())) {
                System.out.println("❌ Usuario sin permisos para ver orden " + orderId);
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
            
            System.out.println("✅ Estado de orden " + orderId + ": " + order.getStatus());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            System.err.println("❌ Orden no encontrada: " + orderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Pedido no encontrado"));
        } catch (Exception e) {
            System.err.println("❌ Error verificando estado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al verificar estado del pago"));
        }
    }
}
