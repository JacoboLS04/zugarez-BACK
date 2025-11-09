package com.zugarez.zugarez_BACK.asistencia.repository;

import com.zugarez.zugarez_BACK.asistencia.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByEmpleadoIdAndFechaBetweenOrderByHoraEntradaAsc(Long empleadoId, LocalDate inicio, LocalDate fin);
}
