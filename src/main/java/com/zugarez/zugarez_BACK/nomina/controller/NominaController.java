package com.zugarez.zugarez_BACK.nomina.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.zugarez.model.Empleado;
import com.zugarez.repository.EmpleadoRepository;
import com.zugarez.zugarez_BACK.external.supabase.SupabaseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador mínimo para cálculo de nómina.
 * Endpoint esperado por el frontend: POST /api/nomina/calcular
 */
@RestController
@RequestMapping("/api/nomina")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NominaController {

    private static final Logger log = LoggerFactory.getLogger(NominaController.class);
    private final EmpleadoRepository empleadoRepository;
    private final SupabaseService supabaseService;

    @PostMapping(value = "/calcular", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> calcular(@RequestBody(required = false) CalculoNominaRequest req) {
        // Body opcional y tolerante
        if (req == null) req = new CalculoNominaRequest();
        log.info(">> Calcular nómina (req crudo): {}", req);

        // 1) Resolver empleado (fallback: primer empleado activo si no se envía empleadoId)
        Empleado emp = resolveEmpleado(req.getEmpleadoId());

        // 2) Resolver fechas (acepta alias string y normaliza)
        LocalDate inicio = resolveFecha(req.getInicioRaw(), req.getInicio(), LocalDate.now());
        LocalDate fin = resolveFecha(req.getFinRaw(), req.getFin(), inicio);
        if (fin.isBefore(inicio)) {
            // normaliza rango invertido
            LocalDate tmp = inicio;
            inicio = fin;
            fin = tmp;
        }

        // 3) Resolver montos
        BigDecimal salarioBase = nz(req.getSalarioBaseMensual(), toBigDecimal(emp.getSalarioBase()));
        long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;
        BigDecimal diario = salarioBase.divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        BigDecimal sueldoPeriodo = diario.multiply(BigDecimal.valueOf(dias));

        BigDecimal comisiones = nz(req.getComisiones(), BigDecimal.ZERO);
        BigDecimal bonificaciones = nz(req.getBonificaciones(), BigDecimal.ZERO);
        BigDecimal salarioNeto = sueldoPeriodo.add(comisiones).add(bonificaciones);

        Map<String, Object> resp = Map.of(
                "empleadoId", emp.getId(),
                "empleado", emp.getNombres() + " " + emp.getApellidos(),
                "periodo", Map.of("inicio", inicio, "fin", fin, "dias", dias),
                "salarioBaseMensual", salarioBase,
                "sueldoPeriodo", sueldoPeriodo,
                "comisiones", comisiones,
                "bonificaciones", bonificaciones,
                "salarioNeto", salarioNeto,
                "estado", "CALCULADO"
        );

        // Persistir cálculo en Supabase
        try {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("empleado_id", emp.getId());
            row.put("empleado_nombre", emp.getNombres() + " " + emp.getApellidos());
            row.put("inicio", inicio);
            row.put("fin", fin);
            row.put("dias", dias);
            row.put("salario_base_mensual", salarioBase);
            row.put("sueldo_periodo", sueldoPeriodo);
            row.put("comisiones", comisiones);
            row.put("bonificaciones", bonificaciones);
            row.put("salario_neto", salarioNeto);
            row.put("creado_en", OffsetDateTime.now());
            supabaseService.insert(supabaseService.getNominaTable(), row);
        } catch (Exception e) {
            log.warn("No se pudo guardar cálculo en Supabase: {}", e.getMessage());
        }

        return ResponseEntity.ok(resp);
    }

    // --- helpers ---

    private Empleado resolveEmpleado(Long empleadoId) {
        if (empleadoId != null) {
            return empleadoRepository.findById(empleadoId)
                    .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));
        }
        // fallback: primer empleado activo; si no hay, primero de la lista
        Optional<Empleado> activo = Optional.empty();
        try {
            // si existe este método en tu repo (se usa en el servicio)
            activo = empleadoRepository.findByActivoTrue().stream().findFirst();
        } catch (Exception ignored) {}
        return activo.or(() -> empleadoRepository.findAll().stream().findFirst())
                .orElseThrow(() -> new EntityNotFoundException("No hay empleados registrados"));
    }

    private LocalDate resolveFecha(String raw, LocalDate parsed, LocalDate def) {
        if (parsed != null) return parsed;
        if (raw == null || raw.isBlank()) return def;
        // acepta "yyyy-MM-dd" o fecha ISO completa, toma primeros 10 caracteres
        String s = raw.trim();
        if (s.length() >= 10) s = s.substring(0, 10);
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            log.warn("No se pudo parsear fecha '{}', usando default {}", raw, def);
            return def;
        }
    }

    private BigDecimal nz(BigDecimal in, BigDecimal def) {
        return in == null ? def : in;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(val.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    @Data
    public static class CalculoNominaRequest {
        @JsonAlias({"empleadoId", "employeeId", "idEmpleado"})
        private Long empleadoId;

        // Acepta LocalDate (cuando el front manda 'yyyy-MM-dd') o raw string (cuando manda ISO completa u otro alias)
        private LocalDate inicio;
        private LocalDate fin;

        @JsonAlias({"inicio", "fechaInicio", "start"})
        private String inicioRaw;

        @JsonAlias({"fin", "fechaFin", "end"})
        private String finRaw;

        @JsonAlias({"comisiones", "commission"})
        private BigDecimal comisiones;

        @JsonAlias({"bonificaciones", "bonus", "bonos"})
        private BigDecimal bonificaciones;

        // Permite override de salario base mensual si el front lo envía
        @JsonAlias({"salarioBase", "salarioBaseMensual", "sueldoBase"})
        private BigDecimal salarioBaseMensual;
    }
}
