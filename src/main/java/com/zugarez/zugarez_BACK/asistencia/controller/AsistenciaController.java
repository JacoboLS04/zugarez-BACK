package com.zugarez.zugarez_BACK.asistencia.controller;

import com.zugarez.zugarez_BACK.asistencia.entity.Asistencia;
import com.zugarez.zugarez_BACK.asistencia.repository.AsistenciaRepository;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaController.class);

    private final AsistenciaRepository asistenciaRepository;

    public AsistenciaController(AsistenciaRepository asistenciaRepository) {
        this.asistenciaRepository = asistenciaRepository;
    }

    @PostMapping(value = "/entrada", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> registrarEntrada(@RequestBody(required = false) EntradaRequest req) {
        if (req == null) req = new EntradaRequest();
        log.info(">> Registrar entrada: empleadoId={}, turno={}, obs={}", req.getEmpleadoId(), req.getTurno(), req.getObservaciones());

        OffsetDateTime ahora = OffsetDateTime.now();
        Asistencia a = new Asistencia();
        a.setEmpleadoId(req.getEmpleadoId());
        a.setTurno(req.getTurno() == null ? "GENERAL" : req.getTurno());
        a.setObservaciones(req.getObservaciones() == null ? "" : req.getObservaciones());
        a.setHoraEntrada(ahora);
        a.setFecha(ahora.toLocalDate());
        // horas trabajadas / extras se calculan al registrar salida, por ahora null
        Asistencia guardado = asistenciaRepository.save(a);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", guardado.getId());
        resp.put("empleadoId", guardado.getEmpleadoId());
        resp.put("fecha", guardado.getFecha());
        resp.put("horaEntrada", guardado.getHoraEntrada());
        resp.put("horaSalida", guardado.getHoraSalida());
        resp.put("turno", guardado.getTurno());
        resp.put("horasTrabajadas", guardado.getHorasTrabajadas());
        resp.put("horasExtras", guardado.getHorasExtras());
        resp.put("observaciones", guardado.getObservaciones());
        resp.put("createdAt", guardado.getCreatedAt());
        resp.put("status", "REGISTRADA");
        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/empleado/{empleadoId}", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> listarPorEmpleadoYRango(
            @PathVariable Long empleadoId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin
    ) {
        log.info(">> Listar asistencia: empleadoId={}, inicio={}, fin={}", empleadoId, inicio, fin);
        List<Asistencia> registros = asistenciaRepository
                .findByEmpleadoIdAndFechaBetweenOrderByHoraEntradaAsc(empleadoId, inicio, fin);

        List<Map<String, Object>> data = registros.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", a.getId());
            m.put("empleadoId", a.getEmpleadoId());
            m.put("fecha", a.getFecha());
            m.put("horaEntrada", a.getHoraEntrada());
            m.put("horaSalida", a.getHoraSalida());
            m.put("turno", a.getTurno());
            m.put("horasTrabajadas", a.getHorasTrabajadas());
            m.put("horasExtras", a.getHorasExtras());
            m.put("observaciones", a.getObservaciones());
            m.put("createdAt", a.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(data);
    }

    @Data
    public static class EntradaRequest {
        private Long empleadoId;
        private String turno;
        private String observaciones;
    }
}
