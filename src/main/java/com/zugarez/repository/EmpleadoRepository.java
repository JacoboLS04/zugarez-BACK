package com.zugarez.repository;

import com.zugarez.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByDni(String dni);
    Optional<Empleado> findByEmail(String email);
    List<Empleado> findByActivoTrue();
    List<Empleado> findByPuestoId(Long puestoId);
}
