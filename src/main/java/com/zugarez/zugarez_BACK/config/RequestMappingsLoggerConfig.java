package com.zugarez.zugarez_BACK.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Conditional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ConditionalOnBean(RequestMappingHandlerMapping.class)
public class RequestMappingsLoggerConfig {
    private static final Logger log = LoggerFactory.getLogger(RequestMappingsLoggerConfig.class);

    @Bean
    public ApplicationRunner mappingsLogger(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mapping) { // forzar bean principal
        return args -> {
            log.info("=== MAPPINGS REGISTRADOS ===");
            mapping.getHandlerMethods().forEach((info, method) ->
                    log.info("{} -> {}#{}", info.getPatternsCondition(), method.getBeanType().getSimpleName(), method.getMethod().getName())
            );
            log.info("=== FIN MAPPINGS ===");
        };
    }
}
