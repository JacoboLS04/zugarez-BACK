package com.zugarez.zugarez_BACK.nomina.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.zugarez.model.Empleado;
import com.zugarez.repository.EmpleadoRepository;
import com.zugarez.zugarez_BACK.asistencia.repository.AsistenciaRepository;
import com.zugarez.zugarez_BACK.asistencia.entity.Asistencia;
import com.zugarez.zugarez_BACK.nomina.entity.NominaCalculo;
import com.zugarez.zugarez_BACK.nomina.repository.NominaCalculoRepository;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final AsistenciaRepository asistenciaRepository;
    private final NominaCalculoRepository nominaCalculoRepository;
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

        // calcular horas trabajadas y extras desde asistencia del período
        BigDecimal horasTrabajadas = calcularHorasTrabajadas(emp.getId(), inicio, fin);
        BigDecimal horasExtras = horasTrabajadas.subtract(BigDecimal.valueOf(8L * dias)).max(BigDecimal.ZERO);

        // pago horas extras (tarifa 1.5x sobre tarifa horaria base)
        BigDecimal tarifaHora = salarioBase.divide(BigDecimal.valueOf(30L * 8L), 4, RoundingMode.HALF_UP);
        BigDecimal pagoHorasExtras = horasExtras.multiply(tarifaHora.multiply(BigDecimal.valueOf(1.5))).setScale(2, RoundingMode.HALF_UP);

        // ingresos y deducciones
        BigDecimal totalIngresos = sueldoPeriodo.add(pagoHorasExtras).add(comisiones).add(bonificaciones);
        BigDecimal essalud = salarioBase.multiply(BigDecimal.valueOf(0.09)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal onp = salarioBase.multiply(BigDecimal.valueOf(0.13)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDeducciones = essalud.add(onp);
        BigDecimal netoPagar = totalIngresos.subtract(totalDeducciones);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("empleadoId", emp.getId());
        resp.put("empleado", emp.getNombres() + " " + emp.getApellidos());
        resp.put("periodo", Map.of("inicio", inicio, "fin", fin, "dias", dias));
        resp.put("salarioBaseMensual", salarioBase);
        resp.put("sueldoPeriodo", sueldoPeriodo);
        resp.put("horasTrabajadas", horasTrabajadas);
        resp.put("horasExtras", horasExtras);
        resp.put("pagoHorasExtras", pagoHorasExtras);
        resp.put("comisiones", comisiones);
        resp.put("bonificaciones", bonificaciones);
        resp.put("totalIngresos", totalIngresos);
        resp.put("essalud", essalud);
        resp.put("onp", onp);
        resp.put("totalDeducciones", totalDeducciones);
        resp.put("salarioNeto", netoPagar);
        resp.put("estado", "CALCULADO");

        // persistir cálculo en BD
        NominaCalculo nc = new NominaCalculo();
        nc.setEmpleadoId(emp.getId());
        nc.setEmpleadoNombre(emp.getNombres() + " " + emp.getApellidos());
        nc.setInicio(inicio);
        nc.setFin(fin);
        nc.setDias((int) dias);
        nc.setSalarioBaseMensual(salarioBase.setScale(2, RoundingMode.HALF_UP));
        nc.setHorasTrabajadas(horasTrabajadas.setScale(2, RoundingMode.HALF_UP));
        nc.setHorasExtras(horasExtras.setScale(2, RoundingMode.HALF_UP));
        nc.setPagoHorasExtras(pagoHorasExtras);
        nc.setComisiones(comisiones.setScale(2, RoundingMode.HALF_UP));
        nc.setBonificaciones(bonificaciones.setScale(2, RoundingMode.HALF_UP));
        nc.setTotalIngresos(totalIngresos);
        nc.setEssalud(essalud);
        nc.setOnp(onp);
        nc.setTotalDeducciones(totalDeducciones);
        nc.setNetoPagar(netoPagar);
        NominaCalculo saved = nominaCalculoRepository.save(nc);

        // opcional: replicar en Supabase
        try {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("empleado_id", saved.getEmpleadoId());
            row.put("empleado_nombre", saved.getEmpleadoNombre());
            row.put("inicio", saved.getInicio());
            row.put("fin", saved.getFin());
            row.put("dias", saved.getDias());
            row.put("salario_base_mensual", saved.getSalarioBaseMensual());
            row.put("horas_trabajadas", saved.getHorasTrabajadas());
            row.put("horas_extras", saved.getHorasExtras());
            row.put("pago_horas_extras", saved.getPagoHorasExtras());
            row.put("comisiones", saved.getComisiones());
            row.put("bonificaciones", saved.getBonificaciones());
            row.put("total_ingresos", saved.getTotalIngresos());
            row.put("essalud", saved.getEssalud());
            row.put("onp", saved.getOnp());
            row.put("total_deducciones", saved.getTotalDeducciones());
            row.put("neto_pagar", saved.getNetoPagar());
            row.put("creado_en", OffsetDateTime.now());
            supabaseService.insert(supabaseService.getNominaTable(), row);
        } catch (Exception ex) {
            log.warn("Supabase omitido: {}", ex.getMessage());
        }

        return ResponseEntity.ok(resp);
    }

    @GetMapping(value = "/calculos", produces = "application/json")
    public ResponseEntity<List<Map<String, Object>>> listarCalculos(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin,
            @RequestParam(required = false) Long empleadoId
    ) {
        List<NominaCalculo> lista = (empleadoId == null)
                ? nominaCalculoRepository.findByInicioGreaterThanEqualAndFinLessThanEqualOrderByCreadoEnDesc(inicio, fin)
                : nominaCalculoRepository.findByEmpleadoIdAndInicioGreaterThanEqualAndFinLessThanEqualOrderByCreadoEnDesc(empleadoId, inicio, fin);

        List<Map<String, Object>> out = lista.stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", n.getId());
            m.put("empleadoId", n.getEmpleadoId());
            m.put("empleado", n.getEmpleadoNombre());
            m.put("inicio", n.getInicio());
            m.put("fin", n.getFin());
            m.put("dias", n.getDias());
            m.put("salarioBaseMensual", n.getSalarioBaseMensual());
            m.put("horasTrabajadas", n.getHorasTrabajadas());
            m.put("horasExtras", n.getHorasExtras());
            m.put("pagoHorasExtras", n.getPagoHorasExtras());
            m.put("comisiones", n.getComisiones());
            m.put("bonificaciones", n.getBonificaciones());
            m.put("totalIngresos", n.getTotalIngresos());
            m.put("essalud", n.getEssalud());
            m.put("onp", n.getOnp());
            m.put("totalDeducciones", n.getTotalDeducciones());
            m.put("salarioNeto", n.getNetoPagar());
            m.put("creadoEn", n.getCreadoEn());
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(out);
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

    private BigDecimal calcularHorasTrabajadas(Long empleadoId, LocalDate inicio, LocalDate fin) {
        List<Asistencia> registros = asistenciaRepository
                .findByEmpleadoIdAndFechaBetweenOrderByHoraEntradaAsc(empleadoId, inicio, fin);
        double total = 0.0;
        for (Asistencia a : registros) {
            if (a.getHoraEntrada() != null && a.getHoraSalida() != null) {
                Duration d = Duration.between(a.getHoraEntrada(), a.getHoraSalida());
                total += d.toMinutes() / 60.0;
            }
        }
        return BigDecimal.valueOf(Math.round(total * 100.0) / 100.0);
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
