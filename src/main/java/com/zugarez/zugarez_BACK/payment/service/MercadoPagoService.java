package com.zugarez.zugarez_BACK.payment.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import com.zugarez.zugarez_BACK.payment.entity.Order;
import com.zugarez.zugarez_BACK.payment.entity.OrderItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token:}")
    private String accessToken;

    @Value("${mercadopago.public.key:}")
    private String publicKey;

    @PostConstruct
    public void init() {
        if (accessToken == null || accessToken.isEmpty() || accessToken.equals("TEST-YOUR_ACCESS_TOKEN_HERE")) {
            System.err.println("⚠️ WARNING: MercadoPago Access Token no configurado.");
        } else {
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("✅ MercadoPago configurado correctamente");
            System.out.println("Access Token: " + accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
            
            if (accessToken.startsWith("TEST-")) {
                System.out.println("🧪 Modo SANDBOX activado");
                System.out.println("");
                System.out.println("📋 PARA PROBAR PAGOS EN SANDBOX:");
                System.out.println("1. Al abrir el checkout de MercadoPago");
                System.out.println("2. Ingresa el email del comprador de prueba");
                System.out.println("3. Usuario: TESTUSER7191328507680256966");
                System.out.println("4. Contraseña: p4mhJvbM7Z");
                System.out.println("5. Usa tarjeta de prueba: 5031 7557 3453 0604");
            } else {
                System.out.println("🔴 Modo PRODUCCIÓN activado");
            }
        }
        
        if (publicKey == null || publicKey.isEmpty()) {
            System.err.println("⚠️ WARNING: MercadoPago Public Key no configurado.");
        } else {
            System.out.println("Public Key: " + publicKey.substring(0, Math.min(20, publicKey.length())) + "...");
        }
    }

    public Preference createPreference(Order order) {
        System.out.println("=== CREANDO PREFERENCIA MERCADOPAGO ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Total: " + order.getTotal());
        System.out.println("Items: " + order.getItems().size());
        
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("MercadoPago no está configurado.");
        }
        
        try {
            List<PreferenceItemRequest> items = new ArrayList<>();
            
            // Agregar productos
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() == null) {
                    throw new RuntimeException("Item sin producto asociado");
                }
                
                System.out.println("Item: " + item.getProduct().getName() + 
                    " | Qty: " + item.getQuantity() + 
                    " | Price: " + item.getPrice());
                
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(String.valueOf(item.getProduct().getId()))
                    .title(item.getProduct().getName())
                    .description(item.getProduct().getDescription())
                    .pictureUrl(item.getProduct().getUrlImage())
                    .categoryId("others")
                    .quantity(item.getQuantity())
                    .currencyId("COP")
                    .unitPrice(item.getPrice())
                    .build();
                
                items.add(itemRequest);
            }

            // Agregar impuesto
            if (order.getTax().compareTo(BigDecimal.ZERO) > 0) {
                PreferenceItemRequest taxItem = PreferenceItemRequest.builder()
                    .id("tax")
                    .title("Impuesto (5%)")
                    .description("Impuesto sobre la venta")
                    .categoryId("others")
                    .quantity(1)
                    .currencyId("COP")
                    .unitPrice(order.getTax())
                    .build();
                
                items.add(taxItem);
            }

            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("https://zugarez.vercel.app/payment/success")
                .failure("https://zugarez.vercel.app/payment/failure")
                .pending("https://zugarez.vercel.app/payment/pending")
                .build();

            // ✅ CONFIGURACIÓN CLAVE: No especificar payer para modo sandbox
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(order.getId().toString())
                .statementDescriptor("ZUGAREZ")
                // ✅ NO incluir .payer() - Permitir login manual en checkout
                .build();

            System.out.println("Creando preferencia...");
            
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            
            System.out.println("✅ Preferencia creada: " + preference.getId());
            System.out.println("Init Point: " + preference.getInitPoint());
            System.out.println("Sandbox Init Point: " + preference.getSandboxInitPoint());
            
            return preference;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR creando preferencia");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear preferencia de pago: " + e.getMessage(), e);
        }
    }

    public String getPublicKey() {
        if (publicKey == null || publicKey.isEmpty()) {
            throw new RuntimeException("MercadoPago Public Key no configurado");
        }
        return publicKey;
    }
}
