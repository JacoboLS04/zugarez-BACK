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
            System.err.println("⚠️ WARNING: MercadoPago Access Token no configurado. Configura MERCADOPAGO_ACCESS_TOKEN en las variables de entorno.");
        } else {
            MercadoPagoConfig.setAccessToken(accessToken);
            System.out.println("✅ MercadoPago configurado correctamente");
            System.out.println("Access Token: " + accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
        }
        
        if (publicKey == null || publicKey.isEmpty() || publicKey.equals("TEST-YOUR_PUBLIC_KEY_HERE")) {
            System.err.println("⚠️ WARNING: MercadoPago Public Key no configurado. Configura MERCADOPAGO_PUBLIC_KEY en las variables de entorno.");
        } else {
            System.out.println("Public Key: " + publicKey.substring(0, Math.min(20, publicKey.length())) + "...");
        }
    }

    public Preference createPreference(Order order) {
        System.out.println("=== CREANDO PREFERENCIA MERCADOPAGO ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Subtotal: " + order.getSubtotal());
        System.out.println("Tax: " + order.getTax());
        System.out.println("Total: " + order.getTotal());
        System.out.println("Items: " + order.getItems().size());
        
        if (accessToken == null || accessToken.isEmpty()) {
            throw new RuntimeException("MercadoPago no está configurado. Verifica las variables de entorno.");
        }
        
        try {
            List<PreferenceItemRequest> items = new ArrayList<>();
            
            // Agregar productos
            for (OrderItem item : order.getItems()) {
                if (item.getProduct() == null) {
                    throw new RuntimeException("Item sin producto asociado");
                }
                
                System.out.println("Agregando item: " + item.getProduct().getName() + 
                    " | Cantidad: " + item.getQuantity() + 
                    " | Precio unitario: " + item.getPrice() +
                    " | Subtotal: " + item.getSubtotal());
                
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(String.valueOf(item.getProduct().getId()))
                    .title(item.getProduct().getName() != null ? item.getProduct().getName() : "Producto")
                    .description(item.getProduct().getDescription() != null ? item.getProduct().getDescription() : "")
                    .pictureUrl(item.getProduct().getUrlImage())
                    .categoryId("others")
                    .quantity(item.getQuantity())
                    .currencyId("COP")
                    .unitPrice(item.getPrice())
                    .build();
                
                items.add(itemRequest);
            }

            // ✅ Agregar el impuesto como un item separado
            if (order.getTax().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Agregando impuesto como item: " + order.getTax());
                
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

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(order.getId().toString())
                .statementDescriptor("ZUGAREZ")
                .build();

            System.out.println("Creando preferencia con MercadoPago...");
            System.out.println("Total de items enviados a MP: " + items.size());
            
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            
            System.out.println("✅ Preferencia creada exitosamente");
            System.out.println("Preference ID: " + preference.getId());
            System.out.println("Sandbox Init Point: " + preference.getSandboxInitPoint());
            
            return preference;
            
        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO creando preferencia de MercadoPago");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Tipo: " + e.getClass().getName());
            e.printStackTrace();
            throw new RuntimeException("Error al crear preferencia de pago: " + e.getMessage(), e);
        }
    }

    public String getPublicKey() {
        if (publicKey == null || publicKey.isEmpty() || publicKey.equals("TEST-YOUR_PUBLIC_KEY_HERE")) {
            throw new RuntimeException("MercadoPago Public Key no configurado");
        }
        return publicKey;
    }
}
