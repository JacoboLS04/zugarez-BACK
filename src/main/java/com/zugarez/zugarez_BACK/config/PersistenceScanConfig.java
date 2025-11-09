package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Asegúrate de eliminar/renombrar cualquier entidad duplicada.
 * Aquí se escanean solo las entidades necesarias del dominio y de seguridad.
 */
@Configuration
@EntityScan(basePackages = {
        "com.zugarez.model",                             // Entidades de dominio (Empleado, Puesto, etc.)
        "com.zugarez.zugarez_BACK.security.entity"       // Entidades de seguridad (UserEntity, etc.)
})
@EnableJpaRepositories(basePackages = {
        "com.zugarez.repository",
        "com.zugarez.zugarez_BACK.security.repository"
})
public class PersistenceScanConfig {
    // ...existing code...
}
