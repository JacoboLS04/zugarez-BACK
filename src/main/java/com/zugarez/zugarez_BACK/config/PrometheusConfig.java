package com.zugarez.zugarez_BACK.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PrometheusConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return (MeterRegistry registry) -> registry.config().commonTags(
            "application", "zugarez-backend",
            "environment", "production",
            "platform", "koyeb"
        );
    }
}
