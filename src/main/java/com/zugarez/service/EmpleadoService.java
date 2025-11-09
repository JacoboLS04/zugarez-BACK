package com.zugarez.service;

import com.zugarez.dto.EmpleadoDTO;
import com.zugarez.model.Puesto;
import com.zugarez.repository.EmpleadoRepository;
import com.zugarez.repository.PuestoRepository;
import com.zugarez.model.Empleado; // entidad única utilizada por el repositorio
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final PuestoRepository puestoRepository;

    @Transactional
    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) {
        log.info(">>> SERVICE: Creación empleado (DNI={}, Email={})", dto.getDni(), dto.getEmail());

        if (empleadoRepository.findByDni(dto.getDni()).isPresent())
            throw new RuntimeException("Ya existe un empleado con este DNI");
        if (empleadoRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Ya existe un empleado con este email");

        Empleado empleado = new Empleado();
        asignarCamposBasicos(empleado, dto);

        if (dto.getPuestoId() != null) {
            Puesto puesto = puestoRepository.findById(dto.getPuestoId())
                    .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
            empleado.setPuesto(puesto);
        }

        Empleado guardado = empleadoRepository.save(empleado);
        log.info(">>> Empleado guardado ID={}", guardado.getId());
        return convertirADTO(guardado);
    }

    @Transactional
    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        asignarCamposBasicos(empleado, dto);

        if (dto.getPuestoId() != null) {
            Puesto puesto = puestoRepository.findById(dto.getPuestoId())
                    .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
            empleado.setPuesto(puesto);
        }

        return convertirADTO(empleadoRepository.save(empleado));
    }

    @Transactional
    public void desactivarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        empleado.setActivo(false);
        empleadoRepository.save(empleado);
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerEmpleadosActivos() {
        return empleadoRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerTodos() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmpleadoDTO obtenerEmpleadoPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        return convertirADTO(empleado);
    }

    private void asignarCamposBasicos(Empleado empleado, EmpleadoDTO dto) {
        empleado.setNombres(dto.getNombres());
        empleado.setApellidos(dto.getApellidos());
        empleado.setDni(dto.getDni());
        empleado.setEmail(dto.getEmail());
        empleado.setTelefono(dto.getTelefono());
        empleado.setFechaContratacion(dto.getFechaContratacion());
        empleado.setSalarioBase(dto.getSalarioBase());
        empleado.setTipoContrato(dto.getTipoContrato());
        empleado.setCuentaBancaria(dto.getCuentaBancaria());
        empleado.setBanco(dto.getBanco());
        if (empleado.getActivo() == null) empleado.setActivo(true);
    }

    private EmpleadoDTO convertirADTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        dto.setNombres(empleado.getNombres());
        dto.setApellidos(empleado.getApellidos());
        dto.setDni(empleado.getDni());
        dto.setEmail(empleado.getEmail());
        dto.setTelefono(empleado.getTelefono());
        dto.setFechaContratacion(empleado.getFechaContratacion());
        dto.setSalarioBase(empleado.getSalarioBase());
        dto.setTipoContrato(empleado.getTipoContrato());
        dto.setCuentaBancaria(empleado.getCuentaBancaria());
        dto.setBanco(empleado.getBanco());
        dto.setActivo(empleado.getActivo());
        if (empleado.getPuesto() != null) {
            dto.setPuestoId(empleado.getPuesto().getId());
            dto.setPuestoNombre(empleado.getPuesto().getNombre());
        }
        return dto;
    }
}
