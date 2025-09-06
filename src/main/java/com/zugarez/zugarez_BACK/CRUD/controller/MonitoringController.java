package com.zugarez.zugarez_BACK.CRUD.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private static final Logger logger = LoggerFactory.getLogger(MonitoringController.class);
    
    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("environment", System.getenv("NODE_ENV"));
        health.put("platform", System.getenv("PLATFORM"));
        health.put("lokiConfigured", System.getenv("LOKI_URL") != null);
        
        // Log para testing
        logger.info("Health check accessed from platform: {}", System.getenv("PLATFORM"));
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<String> metrics() {
        StringBuilder metrics = new StringBuilder();
        metrics.append("# HELP app_info Application information\n");
        metrics.append("# TYPE app_info gauge\n");
        metrics.append("app_info{version=\"1.0\",platform=\"koyeb\"} 1\n");
        metrics.append("# HELP app_uptime_seconds Application uptime\n");
        metrics.append("# TYPE app_uptime_seconds counter\n");
        metrics.append("app_uptime_seconds ").append(System.currentTimeMillis() / 1000).append("\n");
        
        return ResponseEntity.ok(metrics.toString());
    }
    
    @GetMapping("/test-logs")
    public ResponseEntity<String> testLogs() {
        // Incrementar contador personalizado
        Counter.builder("zugarez_test_logs_total")
                .description("Total test logs generated")
                .tag("endpoint", "/monitoring/test-logs")
                .register(meterRegistry)
                .increment();
        
        logger.info("🚀 Test log - INFO level");
        logger.warn("⚠️ Test log - WARN level");
        logger.error("❌ Test log - ERROR level");
        logger.debug("🔍 Test log - DEBUG level");
        
        return ResponseEntity.ok("Test logs generated successfully");
    }
    
    @GetMapping("/generate-metrics")
    public ResponseEntity<String> generateMetrics() {
        // Generar métricas de prueba
        Counter.builder("zugarez_api_calls_total")
                .description("Total API calls")
                .tag("endpoint", "/monitoring/generate-metrics")
                .register(meterRegistry)
                .increment();
                
        logger.info("🔍 [METRICS] Custom metrics generated for Grafana testing");
        return ResponseEntity.ok("Custom metrics generated! Check /actuator/prometheus");
    }
}

