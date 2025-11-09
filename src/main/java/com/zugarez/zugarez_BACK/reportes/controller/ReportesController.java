package com.zugarez.zugarez_BACK.reportes.controller;

import com.zugarez.zugarez_BACK.nomina.entity.NominaCalculo;
import com.zugarez.zugarez_BACK.nomina.repository.NominaCalculoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reportes de nómina por período.
 * Devuelve siempre un objeto con:
 *  - resumen: objeto con totales (nunca null)
 *  - items: lista de detalles (nunca null)
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportesController {

    private final NominaCalculoRepository nominaCalculoRepository;

    @GetMapping("/nomina/periodo")
    public ResponseEntity<Map<String, Object>> reporteNominaPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fin,
            @RequestParam(required = false) Long empleadoId
    ) {
        List<NominaCalculo> lista = empleadoId == null
                ? nominaCalculoRepository.findByInicioGreaterThanEqualAndFinLessThanEqualOrderByCreadoEnDesc(inicio, fin)
                : nominaCalculoRepository.findByEmpleadoIdAndInicioGreaterThanEqualAndFinLessThanEqualOrderByCreadoEnDesc(empleadoId, inicio, fin);

        // Items normalizados (sin nulls)
        List<Map<String, Object>> items = lista.stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", n.getId());
            m.put("empleadoId", n.getEmpleadoId());
            m.put("empleadoNombre", nn(n.getEmpleadoNombre()));
            m.put("periodoInicio", n.getInicio());
            m.put("periodoFin", n.getFin());
            m.put("dias", n.getDias() == null ? 0 : n.getDias());
            m.put("salarioBaseMensual", nz(n.getSalarioBaseMensual()));
            m.put("horasTrabajadas", nz(n.getHorasTrabajadas()));
            m.put("horasExtras", nz(n.getHorasExtras()));
            m.put("pagoHorasExtras", nz(n.getPagoHorasExtras()));
            m.put("comisiones", nz(n.getComisiones()));
            m.put("bonificaciones", nz(n.getBonificaciones()));
            m.put("totalIngresos", nz(n.getTotalIngresos()));
            m.put("essalud", nz(n.getEssalud()));
            m.put("onp", nz(n.getOnp()));
            m.put("totalDeducciones", nz(n.getTotalDeducciones()));
            m.put("salarioNeto", nz(n.getNetoPagar()));
            m.put("estado", nn(n.getEstado()));
            m.put("fechaPago", n.getFechaPago());
            return m;
        }).collect(Collectors.toList());

        // Resumen agregado
        Set<Long> empleadosUnicos = lista.stream()
                .map(NominaCalculo::getEmpleadoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("periodo", Map.of("inicio", inicio, "fin", fin));
        resumen.put("totalCalculos", lista.size());
        resumen.put("empleados", empleadosUnicos.size());
        resumen.put("totalHoras", sum(lista, NominaCalculo::getHorasTrabajadas));
        resumen.put("totalExtras", sum(lista, NominaCalculo::getHorasExtras));
        resumen.put("totalIngresos", sum(lista, NominaCalculo::getTotalIngresos));
        resumen.put("totalDeducciones", sum(lista, NominaCalculo::getTotalDeducciones));
        resumen.put("totalNeto", sum(lista, NominaCalculo::getNetoPagar));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("resumen", resumen);
        resp.put("items", items);
        return ResponseEntity.ok(resp);
    }

    private BigDecimal sum(List<NominaCalculo> lista, java.util.function.Function<NominaCalculo, BigDecimal> f) {
        BigDecimal acc = BigDecimal.ZERO;
        for (NominaCalculo n : lista) {
            BigDecimal v = f.apply(n);
            if (v != null) acc = acc.add(v);
        }
        return acc.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : v.setScale(2, RoundingMode.HALF_UP);
    }

    private String nn(String s) {
        return s == null ? "-" : s;
    }
}
