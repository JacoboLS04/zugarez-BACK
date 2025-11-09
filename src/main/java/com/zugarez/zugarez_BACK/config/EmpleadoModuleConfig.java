package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

/*
 * Escaneo específico para el módulo de empleados.
 * Evita reintroducir escaneo masivo que causó conflictos previos.
 */
@Configuration
@ComponentScan(
        basePackages = {
                "com.zugarez.controller",  // EmpleadoController
                "com.zugarez.service"      // EmpleadoService
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = { com.zugarez.exception.GlobalExceptionHandler.class })
        }
)
public class EmpleadoModuleConfig {
    // ...sin lógica adicional...
}