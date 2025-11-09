package com.zugarez.zugarez_BACK.nomina.controller;

import com.zugarez.model.Empleado;
import com.zugarez.repository.EmpleadoRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

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

    @PostMapping("/calcular")
    public ResponseEntity<Map<String, Object>> calcular(@RequestBody CalculoNominaRequest req) {
        log.info(">> Calcular nómina: {}", req);
        Empleado emp = empleadoRepository.findById(req.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Sueldo base mensual -> prorrateo por días del período
        BigDecimal salarioBase = toBigDecimal(emp.getSalarioBase());
        LocalDate inicio = req.getInicio();
        LocalDate fin = req.getFin();
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Período inválido");
        }
        long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;
        BigDecimal diario = salarioBase.divide(BigDecimal.valueOf(30), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal sueldoPeriodo = diario.multiply(BigDecimal.valueOf(dias));

        BigDecimal comisiones = toBigDecimal(req.getComisiones());
        BigDecimal bonificaciones = toBigDecimal(req.getBonificaciones());
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
        return ResponseEntity.ok(resp);
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal bd) return bd;
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(val.toString()); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    @Data
    public static class CalculoNominaRequest {
        private Long empleadoId;
        private LocalDate inicio;
        private LocalDate fin;
        private BigDecimal comisiones = BigDecimal.ZERO;
        private BigDecimal bonificaciones = BigDecimal.ZERO;
    }
}
