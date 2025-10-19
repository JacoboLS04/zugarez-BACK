package com.zugarez.zugarez_BACK.payment.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferencePaymentMethodsRequest;
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
        // ⚠️ FORZAR CREDENCIALES CORRECTAS (temporal)
        accessToken = "APP_USR-6665299493334563-101915-1093769ec14a844034dce5ac73b33946-2932784465";
        publicKey = "APP_USR-717fb670-7294-4084-aa58-328bd30944cd";
        
        if (accessToken == null || accessToken.isEmpty()) {
            System.err.println("⚠️ MercadoPago Access Token no configurado.");
        } else {
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("✅ MercadoPago configurado correctamente");
            System.out.println("Access Token COMPLETO: " + accessToken);
            System.out.println("");
            
            // Verificar el User ID del token
            String[] parts = accessToken.split("-");
            String userId = parts.length > 0 ? parts[parts.length - 1] : "desconocido";
            
            System.out.println("🔍 USER ID DETECTADO: " + userId);
            
            if (userId.equals("2932784465")) {
                System.out.println("✅✅✅ CORRECTO - Usuario vendedor de prueba (2932784465)");
            } else if (userId.equals("1973438030")) {
                System.err.println("❌❌❌ ERROR - Usando cuenta PRINCIPAL (1973438030)");
                System.err.println("❌ Las credenciales NO se actualizaron correctamente");
                throw new RuntimeException("Credenciales incorrectas - revisa application.properties");
            } else {
                System.err.println("⚠️ User ID desconocido: " + userId);
            }
        }
        
        if (publicKey != null && !publicKey.isEmpty()) {
            System.out.println("Public Key: " + publicKey);
        }
    }

    public Preference createPreference(Order order) {
        System.out.println("=== CREANDO PREFERENCIA MERCADOPAGO ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Total: " + order.getTotal());
        
        try {
            List<PreferenceItemRequest> items = new ArrayList<>();
            
            // Agregar productos
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() == null) {
                    throw new RuntimeException("Item sin producto asociado");
                }
                
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

            // ✅ Configuración para permitir todos los métodos de pago sin restricciones
            PreferencePaymentMethodsRequest paymentMethods = PreferencePaymentMethodsRequest.builder()
                .excludedPaymentMethods(new ArrayList<>())
                .excludedPaymentTypes(new ArrayList<>())
                .installments(12)
                .defaultInstallments(1)
                .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(order.getId().toString())
                .statementDescriptor("ZUGAREZ")
                .paymentMethods(paymentMethods)
                .binaryMode(false) // ✅ Importante: permite estados pendientes
                .marketplace("NONE") // ✅ No es marketplace
                .build();

            System.out.println("Creando preferencia en modo sandbox...");
            
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            
            System.out.println("✅ Preferencia creada: " + preference.getId());
            System.out.println("");
            System.out.println("🧪 INSTRUCCIONES SANDBOX:");
            System.out.println("1. Abre la URL en navegador INCÓGNITO");
            System.out.println("2. NO inicies sesión");
            System.out.println("3. Paga como invitado con tarjeta de prueba:");
            System.out.println("   Tarjeta: 5031 7557 3453 0604");
            System.out.println("   CVV: 123 | Venc: 11/25");
            System.out.println("   Nombre: APRO");
            System.out.println("");
            
            return preference;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al crear preferencia: " + e.getMessage(), e);
        }
    }

    public String getPublicKey() {
        if (publicKey == null || publicKey.isEmpty()) {
            throw new RuntimeException("MercadoPago Public Key no configurado");
        }
        return publicKey;
    }
}