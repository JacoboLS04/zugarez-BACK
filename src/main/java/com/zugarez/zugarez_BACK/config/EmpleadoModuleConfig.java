package com.zugarez.zugarez_BACK.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/*
 * Escaneo específico de Controllers y Services en todo com.zugarez.
 * Evita re-registrar @Configuration y otros beans.
 */
@Configuration
@ComponentScan(
        basePackages = "com.zugarez",
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(Controller.class),
                @ComponentScan.Filter(RestController.class),
                @ComponentScan.Filter(Service.class)
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = { com.zugarez.exception.GlobalExceptionHandler.class })
        }
)
public class EmpleadoModuleConfig {
    // ...sin lógica adicional...
}