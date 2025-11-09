package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(
        basePackages = {
                "com.zugarez.controller",
                "com.zugarez.service",
                "com.zugarez.repository",
                "com.zugarez.model",
                "com.zugarez.dto",
                "com.zugarez.zugarez_BACK"
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = { com.zugarez.exception.GlobalExceptionHandler.class }) // excluye el handler externo duplicado
        }
)
@EntityScan(basePackages = {
        "com.zugarez.model",
        "com.zugarez.zugarez_BACK" // si hubiera entidades internas
})
@EnableJpaRepositories(basePackages = {
        "com.zugarez.repository",
        "com.zugarez.zugarez_BACK" // si hubiese repos internos
})
public class PersistenceScanConfig {
    // ...existing code...
}
