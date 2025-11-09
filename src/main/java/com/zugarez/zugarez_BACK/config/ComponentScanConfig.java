package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "com.zugarez.controller",
        "com.zugarez.service",
        "com.zugarez.repository",
        "com.zugarez.dto",
        "com.zugarez.model",
        "com.zugarez.zugarez_BACK" // mantiene el paquete original
})
public class ComponentScanConfig {
    // ...sin lógica adicional...
}
