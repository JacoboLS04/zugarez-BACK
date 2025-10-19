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

            // ✅ Construir URL de sandbox directamente
            String sandboxUrl = "https://sandbox.mercadopago.com.co/checkout/v1/redirect?pref_id=" 
                + order.getMercadopagoPreferenceId();

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("preferenceId", order.getMercadopagoPreferenceId());
            response.put("subtotal", order.getSubtotal());
            response.put("tax", order.getTax());
            response.put("total", order.getTotal());
            response.put("publicKey", mercadoPagoService.getPublicKey());
            response.put("sandboxUrl", sandboxUrl);
            response.put("checkoutUrl", sandboxUrl);

            System.out.println("✅ Sandbox URL generada: " + sandboxUrl);
            System.out.println("");
            System.out.println("📋 INSTRUCCIONES PARA PROBAR EL PAGO:");
            System.out.println("════════════════════════════════════════════════");
            System.out.println("1. Abre la URL de checkout en tu navegador");
            System.out.println("2. MercadoPago te pedirá iniciar sesión con un usuario de prueba");
            System.out.println("");
            System.out.println("🔐 CREDENCIALES DE PRUEBA (Comprador):");
            System.out.println("   Usuario: TESTUSER7191328507680256966");
            System.out.println("   Contraseña: p4mhJvbM7Z");
            System.out.println("");
            System.out.println("💳 TARJETAS DE PRUEBA:");
            System.out.println("   • Aprobada: 5031 7557 3453 0604 | CVV: 123 | Venc: 11/25");
            System.out.println("   • Rechazada: 5031 4332 1540 6351 | CVV: 123 | Venc: 11/25");
            System.out.println("");
            System.out.println("📚 Más tarjetas en: https://www.mercadopago.com.co/developers/es/docs/checkout-pro/additional-content/test-cards");
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
