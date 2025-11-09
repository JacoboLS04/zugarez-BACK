package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Añadido paquete CRUD, inventory y payment para entidades y repositorios.
 */
@Configuration
@EntityScan(basePackages = {
        "com.zugarez.model",                             // Dominio (Empleado, Puesto, etc.)
        "com.zugarez.zugarez_BACK.security.entity",      // Seguridad (UserEntity)
        "com.zugarez.zugarez_BACK.CRUD.entity",          // CRUD (Product, DetallePedido)
        "com.zugarez.zugarez_BACK.inventory.entity",     // Inventario (Lote, etc.)
        "com.zugarez.zugarez_BACK.payment.entity",       // Payment (Order, OrderItem, etc.)
        "com.zugarez.zugarez_BACK.nomina.entity",        // añadido
        "com.zugarez.zugarez_BACK.asistencia.entity",    // añadido
        "com.zugarez.zugarez_BACK.reportes.entity"       // añadido
})
@EnableJpaRepositories(basePackages = {
        "com.zugarez.repository",                        // Repos dominio
        "com.zugarez.zugarez_BACK.security.repository",  // Repos seguridad
        "com.zugarez.zugarez_BACK.CRUD.repository",      // Repos CRUD
        "com.zugarez.zugarez_BACK.inventory.repository", // Repos inventario
        "com.zugarez.zugarez_BACK.payment.repository",   // Repos payment (OrderRepository)
        "com.zugarez.zugarez_BACK.nomina.repository",    // añadido
        "com.zugarez.zugarez_BACK.asistencia.repository", // añadido
        "com.zugarez.zugarez_BACK.reportes.repository"   // añadido
})
public class PersistenceScanConfig {
    // ...existing code...
}
