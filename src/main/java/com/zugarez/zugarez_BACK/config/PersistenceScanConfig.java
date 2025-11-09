package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/*
 * Fuerza el escaneo de paquetes fuera de com.zugarez.zugarez_BACK.*
 * Incluye controllers, services, repositories y entities en com.zugarez.*
 */
@Configuration
@ComponentScan(basePackages = {
        "com.zugarez",              // cubre controller, service, dto, etc.
        "com.zugarez.zugarez_BACK"  // mantiene el árbol original
})
@EntityScan(basePackages = {
        "com.zugarez.model",
        "com.zugarez.zugarez_BACK"  // por si hay entidades dentro del paquete original
})
@EnableJpaRepositories(basePackages = {
        "com.zugarez.repository",
        "com.zugarez.zugarez_BACK"  // repos existentes originales
})
public class PersistenceScanConfig {
    // ...sin lógica adicional...
}
