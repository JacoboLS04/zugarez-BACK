package com.zugarez.zugarez_BACK.CRUD.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("environment", System.getenv("NODE_ENV"));
        health.put("platform", System.getenv("PLATFORM"));
        health.put("lokiConfigured", System.getenv("LOKI_URL") != null);
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/metrics")
    public ResponseEntity<String> metrics() {
        // Métricas básicas de la aplicación
        StringBuilder metrics = new StringBuilder();
        metrics.append("# HELP app_info Application information\n");
        metrics.append("# TYPE app_info gauge\n");
        metrics.append("app_info{version=\"1.0\",platform=\"koyeb\"} 1\n");
        metrics.append("# HELP app_uptime_seconds Application uptime\n");
        metrics.append("# TYPE app_uptime_seconds counter\n");
        metrics.append("app_uptime_seconds ").append(System.currentTimeMillis() / 1000).append("\n");
        
        return ResponseEntity.ok(metrics.toString());
    }
}
