package com.zugarez.zugarez_BACK.controller;

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
        Map<String, Object> data = new HashMap<>();
        data.put("resultType", "vector");
        data.put("result", new Object[]{});
        response.put("data", data);
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
        return ResponseEntity.ok(response);
    }
}
