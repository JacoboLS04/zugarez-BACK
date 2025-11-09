package com.zugarez.zugarez_BACK.asistencia.controller;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Controlador mínimo de asistencia.
 * Endpoint esperado por el frontend: POST /api/asistencia/entrada
 */
@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaController.class);

    @PostMapping("/entrada")
    public ResponseEntity<Map<String, Object>> registrarEntrada(@RequestBody EntradaRequest req) {
        log.info(">> Registrar entrada: {}", req);
        // Aquí podrías persistir asistencia; por ahora devolvemos un ack básico
        Map<String, Object> resp = Map.of(
                "empleadoId", req.getEmpleadoId(),
                "turno", req.getTurno(),
                "observaciones", req.getObservaciones(),
                "horaEntrada", OffsetDateTime.now().toString(),
                "status", "REGISTRADA"
        );
        return ResponseEntity.ok(resp);
    }

    @Data
    public static class EntradaRequest {
        private Long empleadoId;
        private String turno;
        private String observaciones;
    }
}
