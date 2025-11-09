package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Añadido paquete CRUD para entidades y repositorios (Product, DetallePedido, etc.).
 * Elimina cualquier clase duplicada de entidad o handler.
 */
@Configuration
@EntityScan(basePackages = {
        "com.zugarez.model",                             // Dominio (Empleado, Puesto, etc.)
        "com.zugarez.zugarez_BACK.security.entity",       // Seguridad (UserEntity)
        "com.zugarez.zugarez_BACK.CRUD.entity",            // CRUD (Product, DetallePedido)
        "com.zugarez.zugarez_BACK.inventory.entity"       // <-- añadido
})
@EnableJpaRepositories(basePackages = {
        "com.zugarez.repository",                        // Repos dominio
        "com.zugarez.zugarez_BACK.security.repository",  // Repos seguridad
        "com.zugarez.zugarez_BACK.CRUD.repository",       // Repos CRUD (ProductRepository)
        "com.zugarez.zugarez_BACK.inventory.repository"  // <-- añadido
})
public class PersistenceScanConfig {
    // ...existing code...
}
