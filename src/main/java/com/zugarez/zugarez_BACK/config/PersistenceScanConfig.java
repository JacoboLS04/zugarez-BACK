package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/*
 * Asegúrate de eliminar/renombrar la entidad duplicada Empleado en uno de los paquetes.
 */
@Configuration
@EntityScan(basePackages = {
        "com.zugarez.model",                       // Puesto y otras entidades
        "com.zugarez.zugarez_BACK.global.entity"   // Empleado (única versión usada en servicio)
})
@EnableJpaRepositories(basePackages = {
        "com.zugarez.repository",
        "com.zugarez.zugarez_BACK.security.repository"
})
public class PersistenceScanConfig {
    // ...existing code...
}
