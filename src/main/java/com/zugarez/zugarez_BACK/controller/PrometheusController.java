package com.zugarez.zugarez_BACK.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusMeterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PrometheusController {

    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", Map.of(
            "resultType", "vector",
            "result", new Object[]{}
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryPost(@RequestParam String query) {
        return query(query);
    }

    @GetMapping("/status/buildinfo")
    public ResponseEntity<Map<String, Object>> buildinfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", Map.of(
            "version", "2.0.0",
            "revision", "spring-boot-actuator",
            "branch", "main",
            "buildUser", "spring-boot",
            "buildDate", "2024-01-01T00:00:00Z",
            "goVersion", "go1.21"
        ));
        return ResponseEntity.ok(response);
    }
}
