package com.zugarez.repository;

import com.zugarez.model.RegistroAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Long> {
    List<RegistroAsistencia> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate inicio, LocalDate fin);
    
    @Query("SELECT COALESCE(SUM(r.horasTrabajadas), 0.0) FROM RegistroAsistencia r WHERE r.empleado.id = :empleadoId AND r.fecha BETWEEN :inicio AND :fin")
    Double sumHorasTrabajadasByEmpleadoAndPeriodo(@Param("empleadoId") Long empleadoId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT COALESCE(SUM(r.horasExtras), 0.0) FROM RegistroAsistencia r WHERE r.empleado.id = :empleadoId AND r.fecha BETWEEN :inicio AND :fin")
    Double sumHorasExtrasByEmpleadoAndPeriodo(@Param("empleadoId") Long empleadoId, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
}
