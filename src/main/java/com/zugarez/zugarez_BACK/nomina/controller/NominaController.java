package com.zugarez.zugarez_BACK.nomina.controller;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zugarez.model.Empleado;
import com.zugarez.repository.EmpleadoRepository;
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

    @PostMapping(value = "/calcular", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> calcular(@RequestBody CalculoNominaRequest req) {
        log.info(">> Calcular nómina: {}", req);

        Empleado emp = empleadoRepository.findById(req.getEmpleadoId())
                .orElseThrow(() -> new EntityNotFoundException("Empleado no encontrado"));

        LocalDate inicio = req.getInicio();
        LocalDate fin = req.getFin();
        if (inicio == null || fin == null || fin.isBefore(inicio)) {
            throw new IllegalArgumentException("Período inválido");
        }

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
        return ResponseEntity.ok(resp);
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

        @JsonAlias({"inicio", "fechaInicio", "start"})
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate inicio;

        @JsonAlias({"fin", "fechaFin", "end"})
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate fin;

        @JsonAlias({"comisiones", "commission"})
        private BigDecimal comisiones;

        @JsonAlias({"bonificaciones", "bonus", "bonos"})
        private BigDecimal bonificaciones;

        // Permite override de salario base mensual si el front lo envía
        @JsonAlias({"salarioBase", "salarioBaseMensual", "sueldoBase"})
        private BigDecimal salarioBaseMensual;
    }
}
