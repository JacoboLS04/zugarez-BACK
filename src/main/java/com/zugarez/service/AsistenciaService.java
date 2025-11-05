package com.zugarez.service;

import com.zugarez.dto.RegistroAsistenciaDTO;
import com.zugarez.model.Empleado;
import com.zugarez.model.RegistroAsistencia;
import com.zugarez.model.TipoTurno;
import com.zugarez.repository.EmpleadoRepository;
import com.zugarez.repository.RegistroAsistenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaService {
    
    private final RegistroAsistenciaRepository asistenciaRepository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional
    public RegistroAsistenciaDTO registrarEntrada(Long empleadoId, TipoTurno turno) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setEmpleado(empleado);
        registro.setFecha(LocalDate.now());
        registro.setHoraEntrada(LocalDateTime.now());
        registro.setTurno(turno);

        RegistroAsistencia guardado = asistenciaRepository.save(registro);
        return convertirADTO(guardado);
    }

    @Transactional
    public RegistroAsistenciaDTO registrarSalida(Long registroId) {
        RegistroAsistencia registro = asistenciaRepository.findById(registroId)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        if (registro.getHoraSalida() != null) {
            throw new RuntimeException("Ya se registró la salida para este registro");
        }

        registro.setHoraSalida(LocalDateTime.now());
        RegistroAsistencia actualizado = asistenciaRepository.save(registro);
        return convertirADTO(actualizado);
    }

    public List<RegistroAsistenciaDTO> obtenerAsistenciasPorEmpleadoYPeriodo(
            Long empleadoId, LocalDate inicio, LocalDate fin) {
        return asistenciaRepository.findByEmpleadoIdAndFechaBetween(empleadoId, inicio, fin)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private RegistroAsistenciaDTO convertirADTO(RegistroAsistencia registro) {
        RegistroAsistenciaDTO dto = new RegistroAsistenciaDTO();
        dto.setId(registro.getId());
        dto.setEmpleadoId(registro.getEmpleado().getId());
        dto.setEmpleadoNombre(registro.getEmpleado().getNombres() + " " + registro.getEmpleado().getApellidos());
        dto.setFecha(registro.getFecha());
        dto.setHoraEntrada(registro.getHoraEntrada());
        dto.setHoraSalida(registro.getHoraSalida());
        dto.setTurno(registro.getTurno());
        dto.setHorasTrabajadas(registro.getHorasTrabajadas());
        dto.setHorasExtras(registro.getHorasExtras());
        dto.setObservaciones(registro.getObservaciones());
        return dto;
    }
}
