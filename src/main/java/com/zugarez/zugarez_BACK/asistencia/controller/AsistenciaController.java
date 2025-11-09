package com.zugarez.zugarez_BACK.asistencia.controller;

import com.zugarez.zugarez_BACK.asistencia.entity.Asistencia;
import com.zugarez.zugarez_BACK.asistencia.repository.AsistenciaRepository;
import com.zugarez.zugarez_BACK.external.supabase.SupabaseService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asistencia")
@CrossOrigin(origins = "*")
public class AsistenciaController {

    private static final Logger log = LoggerFactory.getLogger(AsistenciaController.class);

    private final AsistenciaRepository asistenciaRepository;
    private final SupabaseService supabaseService;

    public AsistenciaController(AsistenciaRepository asistenciaRepository, SupabaseService supabaseService) {
        this.asistenciaRepository = asistenciaRepository;
        this.supabaseService = supabaseService;
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

        // Persistir también en Supabase
        try {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("empleado_id", guardado.getEmpleadoId());
            row.put("fecha", guardado.getFecha());
            row.put("hora_entrada", guardado.getHoraEntrada());
            row.put("hora_salida", guardado.getHoraSalida());
            row.put("turno", guardado.getTurno());
            row.put("horas_trabajadas", guardado.getHorasTrabajadas());
            row.put("horas_extras", guardado.getHorasExtras());
            row.put("observaciones", guardado.getObservaciones());
            row.put("created_at", guardado.getCreatedAt());
            supabaseService.insert(supabaseService.getAsistenciaTable(), row);
        } catch (Exception e) {
            log.warn("No se pudo insertar en Supabase: {}", e.getMessage());
        }

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

    @PutMapping(value = "/{id}/salida", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> registrarSalida(
            @PathVariable Long id,
            @RequestBody(required = false) SalidaRequest body
    ) {
        Asistencia asistencia = asistenciaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro de asistencia no encontrado"));

        if (asistencia.getHoraSalida() != null) {
            throw new IllegalStateException("La salida ya fue registrada");
        }

        OffsetDateTime horaSalida = (body != null && body.getHoraSalida() != null) ? body.getHoraSalida() : OffsetDateTime.now();
        asistencia.setHoraSalida(horaSalida);

        if (asistencia.getHoraEntrada() != null) {
            Duration d = Duration.between(asistencia.getHoraEntrada(), horaSalida);
            double horas = d.toMinutes() / 60.0;
            double horasRedondeadas = Math.round(horas * 100.0) / 100.0;
            asistencia.setHorasTrabajadas(horasRedondeadas);
            double extras = horasRedondeadas > 8.0 ? horasRedondeadas - 8.0 : 0.0;
            asistencia.setHorasExtras(extras > 0 ? Math.round(extras * 100.0) / 100.0 : 0.0);
        }

        Asistencia actualizado = asistenciaRepository.save(asistencia);

        Map<String, Object> resp = new HashMap<>();
        resp.put("id", actualizado.getId());
        resp.put("empleadoId", actualizado.getEmpleadoId());
        resp.put("fecha", actualizado.getFecha());
        resp.put("horaEntrada", actualizado.getHoraEntrada());
        resp.put("horaSalida", actualizado.getHoraSalida());
        resp.put("turno", actualizado.getTurno());
        resp.put("horasTrabajadas", actualizado.getHorasTrabajadas());
        resp.put("horasExtras", actualizado.getHorasExtras());
        resp.put("observaciones", actualizado.getObservaciones());
        resp.put("createdAt", actualizado.getCreatedAt());
        resp.put("status", "SALIDA_REGISTRADA");

        // Persistir/actualizar también en Supabase (upsert por combinación empleado_id+fecha)
        try {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("empleado_id", actualizado.getEmpleadoId());
            row.put("fecha", actualizado.getFecha());
            row.put("hora_entrada", actualizado.getHoraEntrada());
            row.put("hora_salida", actualizado.getHoraSalida());
            row.put("turno", actualizado.getTurno());
            row.put("horas_trabajadas", actualizado.getHorasTrabajadas());
            row.put("horas_extras", actualizado.getHorasExtras());
            row.put("observaciones", actualizado.getObservaciones());
            row.put("created_at", actualizado.getCreatedAt());
            supabaseService.upsert(supabaseService.getAsistenciaTable(), row, "empleado_id,fecha");
        } catch (Exception e) {
            log.warn("No se pudo upsert en Supabase: {}", e.getMessage());
        }

        return ResponseEntity.ok(resp);
    }

    @Data
    public static class EntradaRequest {
        private Long empleadoId;
        private String turno;
        private String observaciones;
    }

    @Data
    public static class SalidaRequest {
        private OffsetDateTime horaSalida;
    }
}
