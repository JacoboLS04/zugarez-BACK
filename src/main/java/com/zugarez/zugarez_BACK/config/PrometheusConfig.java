package com.zugarez.zugarez_BACK.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

@Configuration
public class PrometheusConfig {

    @Bean(name = "customPrometheusConfig") // Cambiar el nombre del bean para evitar conflictos
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags(
            "application", "zugarez-backend",
            "environment", "production",
            "platform", "koyeb"
        );
    }
}
