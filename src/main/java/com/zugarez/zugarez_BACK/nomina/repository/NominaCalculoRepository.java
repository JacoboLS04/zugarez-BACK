package com.zugarez.zugarez_BACK.nomina.repository;

import com.zugarez.zugarez_BACK.nomina.entity.NominaCalculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface NominaCalculoRepository extends JpaRepository<NominaCalculo, Long> {
    List<NominaCalculo> findByInicioGreaterThanEqualAndFinLessThanEqualOrderByCreadoEnDesc(LocalDate inicio, LocalDate fin);
    List<NominaCalculo> findByEmpleadoIdAndInicioGreaterThanEqualAndFinLessThanEqualOrderByCreadoEnDesc(Long empleadoId, LocalDate inicio, LocalDate fin);

    List<NominaCalculo> findByEstadoOrderByCreadoEnDesc(String estado);
    List<NominaCalculo> findByEmpleadoIdOrderByCreadoEnDesc(Long empleadoId);
}
