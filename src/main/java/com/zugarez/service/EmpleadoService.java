package com.zugarez.service;

import com.zugarez.dto.EmpleadoDTO;
import com.zugarez.model.Empleado;
import com.zugarez.model.Puesto;
import com.zugarez.repository.EmpleadoRepository;
import com.zugarez.repository.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    
    private final EmpleadoRepository empleadoRepository;
    private final PuestoRepository puestoRepository;

    @Transactional
    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) {
        if (empleadoRepository.findByDni(dto.getDni()).isPresent()) {
            throw new RuntimeException("Ya existe un empleado con este DNI");
        }
        if (empleadoRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un empleado con este email");
        }

        Empleado empleado = new Empleado();
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
        empleado.setActivo(true);

        if (dto.getPuestoId() != null) {
            Puesto puesto = puestoRepository.findById(dto.getPuestoId())
                    .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
            empleado.setPuesto(puesto);
        }

        Empleado guardado = empleadoRepository.save(empleado);
        return convertirADTO(guardado);
    }

    @Transactional
    public EmpleadoDTO actualizarEmpleado(Long id, EmpleadoDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        empleado.setNombres(dto.getNombres());
        empleado.setApellidos(dto.getApellidos());
        empleado.setEmail(dto.getEmail());
        empleado.setTelefono(dto.getTelefono());
        empleado.setSalarioBase(dto.getSalarioBase());
        empleado.setTipoContrato(dto.getTipoContrato());
        empleado.setCuentaBancaria(dto.getCuentaBancaria());
        empleado.setBanco(dto.getBanco());

        if (dto.getPuestoId() != null) {
            Puesto puesto = puestoRepository.findById(dto.getPuestoId())
                    .orElseThrow(() -> new RuntimeException("Puesto no encontrado"));
            empleado.setPuesto(puesto);
        }

        Empleado actualizado = empleadoRepository.save(empleado);
        return convertirADTO(actualizado);
    }

    @Transactional
    public void desactivarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        empleado.setActivo(false);
        empleadoRepository.save(empleado);
    }

    public List<EmpleadoDTO> obtenerEmpleadosActivos() {
        return empleadoRepository.findByActivoTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<EmpleadoDTO> obtenerTodos() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public EmpleadoDTO obtenerEmpleadoPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        return convertirADTO(empleado);
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
