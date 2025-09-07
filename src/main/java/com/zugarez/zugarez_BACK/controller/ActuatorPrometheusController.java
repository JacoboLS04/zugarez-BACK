package com.zugarez.zugarez_BACK.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class ActuatorPrometheusController {

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestParam String query) {
        System.out.println("=== PROMETHEUS QUERY ENDPOINT CALLED ===");
        System.out.println("Query: " + query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("resultType", "vector");
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        // Simular respuesta basada en la query
        if (query.contains("jvm_memory_used_bytes")) {
            Map<String, Object> result = new HashMap<>();
            Map<String, String> metric = new HashMap<>();
            metric.put("__name__", "jvm_memory_used_bytes");
            metric.put("area", "heap");
            result.put("metric", metric);
            result.put("value", new Object[]{System.currentTimeMillis() / 1000, "50000000"});
            results.add(result);
        }
        
        data.put("result", results);
        response.put("data", data);
        return ResponseEntity.ok(response);
        
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryPost(@RequestParam String query) {
        System.out.println("=== PROMETHEUS QUERY POST ENDPOINT CALLED ===");
        return query(query);
    }

    @GetMapping("/status/buildinfo")
    public ResponseEntity<Map<String, Object>> buildinfo() {
        System.out.println("=== PROMETHEUS BUILDINFO ENDPOINT CALLED ===");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("version", "2.0.0");
        data.put("revision", "spring-boot-actuator");
        data.put("branch", "main");
        data.put("buildUser", "spring-boot");
        data.put("buildDate", "2024-01-01T00:00:00Z");
        data.put("goVersion", "go1.21");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }
}
