package com.zugarez.zugarez_BACK.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ActuatorPrometheusController {

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        System.out.println("=== TEST ENDPOINT CALLED ===");
        return ResponseEntity.ok("Controller funcionando!");
    }

    @GetMapping("/query_range")
    public ResponseEntity<Map<String, Object>> queryRange(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String step) {
        
        System.out.println("=== PROMETHEUS QUERY_RANGE ENDPOINT CALLED ===");
        System.out.println("Query: " + query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("resultType", "matrix");
        data.put("result", new Object[]{});
        response.put("data", data);
        
        return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "false")
                .body(response);
    }

    @GetMapping("/query")
    public ResponseEntity<Map<String, Object>> query(@RequestParam String query) {
        System.out.println("=== PROMETHEUS QUERY ENDPOINT CALLED ===");
        System.out.println("Query: " + query);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "false");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        Map<String, Object> data = new HashMap<>();
        data.put("resultType", "vector");
        
        // Simular datos reales basados en la query
        Object[] results;
        if (query.contains("jvm_memory_used_bytes")) {
            Map<String, Object> result = new HashMap<>();
            Map<String, String> metric = new HashMap<>();
            metric.put("__name__", "jvm_memory_used_bytes");
            metric.put("area", "heap");
            result.put("metric", metric);
            result.put("value", new Object[]{System.currentTimeMillis() / 1000.0, "45000000"});
            results = new Object[]{result};
        } else {
            results = new Object[]{};
        }
        
        data.put("result", results);
        response.put("data", data);
        return ResponseEntity.ok().headers(headers).body(response);
        
    }

    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> queryPost(@RequestParam String query) {
        System.out.println("=== PROMETHEUS QUERY POST ENDPOINT CALLED ===");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "false");
        
        return ResponseEntity.ok().headers(headers).body(query(query).getBody());
    }

    @GetMapping("/status/buildinfo")
    public ResponseEntity<Map<String, Object>> buildinfo() {
        System.out.println("=== PROMETHEUS BUILDINFO ENDPOINT CALLED ===");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "false");
        
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
        return ResponseEntity.ok().headers(headers).body(response);
    }

    private Object[] generateMetricData(String query) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> metric = new HashMap<>();
        
        if (query.contains("jvm_memory_used_bytes")) {
            metric.put("__name__", "jvm_memory_used_bytes");
            metric.put("area", "heap");
        } else if (query.contains("system_cpu_usage")) {
            metric.put("__name__", "system_cpu_usage");
        } else if (query.contains("process_cpu_usage")) {
            metric.put("__name__", "process_cpu_usage");
        } else if (query.contains("jvm_threads_live")) {
            metric.put("__name__", "jvm_threads_live_threads");
        } else {
            metric.put("__name__", "unknown_metric");
        }
        
        metric.put("application", "zugarez-backend");
        metric.put("instance", "");
        result.put("metric", metric);
        
        // Generar datos de tiempo simulados
        long currentTime = System.currentTimeMillis() / 1000;
        Object[][] values = {
            {currentTime - 120, Math.random() * 100 + ""},
            {currentTime - 60, Math.random() * 100 + ""},
            {currentTime, Math.random() * 100 + ""}
        };
        result.put("values", values);
        
        return new Object[]{result};
    }

    @GetMapping("/label/{label}/values")
    public ResponseEntity<Map<String, Object>> labelValues(@PathVariable String label) {
        System.out.println("=== PROMETHEUS LABEL VALUES ENDPOINT CALLED ===");
        System.out.println("Label: " + label);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "false");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", new String[]{"heap", "nonheap"});
        
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @GetMapping("/labels")
    public ResponseEntity<Map<String, Object>> labels() {
        System.out.println("=== PROMETHEUS LABELS ENDPOINT CALLED ===");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        headers.add("Access-Control-Allow-Headers", "*");
        headers.add("Access-Control-Allow-Credentials", "false");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", new String[]{"__name__", "application", "instance", "area", "id"});
        
        return ResponseEntity.ok().headers(headers).body(response);
    }
}
