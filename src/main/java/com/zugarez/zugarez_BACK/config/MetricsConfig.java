package com.zugarez.zugarez_BACK.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for application metrics using Micrometer.
 * Defines beans for tracking HTTP requests and response times.
 */
@Configuration
public class MetricsConfig {

    /**
     * Creates a counter bean for tracking total HTTP requests.
     * @param meterRegistry the meter registry
     * @return Counter for HTTP requests
     */
    @Bean
    public Counter requestCounter(MeterRegistry meterRegistry) {
        return Counter.builder("http_requests_total")
                .description("Total HTTP requests")
                .register(meterRegistry);
    }

    /**
     * Creates a timer bean for tracking HTTP request duration.
     * @param meterRegistry the meter registry
     * @return Timer for HTTP request duration
     */
    @Bean
    public Timer requestTimer(MeterRegistry meterRegistry) {
        return Timer.builder("http_request_duration")
                .description("HTTP request duration")
                .register(meterRegistry);
    }
}
