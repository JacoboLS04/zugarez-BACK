package com.zugarez.repository;

import com.zugarez.model.EstadoNomina;
import com.zugarez.model.Nomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface NominaRepository extends JpaRepository<Nomina, Long> {
    List<Nomina> findByEmpleadoId(Long empleadoId);
    List<Nomina> findByEstado(EstadoNomina estado);
    List<Nomina> findByPeriodoInicioBetween(LocalDate inicio, LocalDate fin);
    Optional<Nomina> findByEmpleadoIdAndPeriodoInicioAndPeriodoFin(Long empleadoId, LocalDate inicio, LocalDate fin);
}
