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
        if (accessToken == null || accessToken.isEmpty() || accessToken.equals("TEST-YOUR_ACCESS_TOKEN_HERE")) {
            throw new RuntimeException("MercadoPago no está configurado. Configura las credenciales en las variables de entorno.");
        }
        
        try {
            List<PreferenceItemRequest> items = new ArrayList<>();
            
            for (OrderItem item : order.getItems()) {
                // Validar que el producto y sus datos existan
                if (item.getProduct() == null) {
                    throw new RuntimeException("Item sin producto asociado");
                }
                
                PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(String.valueOf(item.getProduct().getId())) // ✅ Convertir Integer a String
                    .title(item.getProduct().getName() != null ? item.getProduct().getName() : "Producto sin nombre")
                    .description(item.getProduct().getDescription() != null ? item.getProduct().getDescription() : "Sin descripción")
                    .pictureUrl(item.getProduct().getUrlImage())
                    .categoryId("others") // Categoría genérica
                    .quantity(item.getQuantity())
                    .currencyId("COP") // Peso colombiano
                    .unitPrice(item.getPrice()) // Ya es BigDecimal, MercadoPago lo acepta
                    .build();
                
                items.add(itemRequest);
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

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            
            System.out.println("✅ Preferencia de MercadoPago creada: " + preference.getId());
            
            return preference;
            
        } catch (Exception e) {
            System.err.println("❌ Error creando preferencia de MercadoPago: " + e.getMessage());
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
