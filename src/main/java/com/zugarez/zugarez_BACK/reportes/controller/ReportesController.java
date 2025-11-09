package com.zugarez.zugarez_BACK.reportes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador mínimo de reportes de nómina.
 * Endpoint esperado por el frontend:
 *   GET /api/reportes/nomina/periodo?inicio=YYYY-MM-DD&fin=YYYY-MM-DD
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReportesController {

    private static final Logger log = LoggerFactory.getLogger(ReportesController.class);

    @GetMapping("/nomina/periodo")
    public ResponseEntity<List<Map<String, Object>>> reporteNominaPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
    ) {
        log.info(">> Reporte de nómina desde {} hasta {}", inicio, fin);
        // Retorna lista vacía por ahora (no 404), la UI podrá mostrar sin datos.
        return ResponseEntity.ok(List.of());
    }
}
