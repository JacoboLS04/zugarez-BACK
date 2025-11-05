package com.zugarez.controller;

import com.zugarez.dto.EmpleadoDTO;
import com.zugarez.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody EmpleadoDTO dto) {
        log.info("Recibida petición para crear empleado: {}", dto);
        try {
            EmpleadoDTO empleado = empleadoService.crearEmpleado(dto);
            log.info("Empleado creado exitosamente: {}", empleado.getId());
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            log.error("Error al crear empleado", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        log.info("Recibida petición para actualizar empleado: {}", id);
        EmpleadoDTO empleado = empleadoService.actualizarEmpleado(id, dto);
        return ResponseEntity.ok(empleado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarEmpleado(@PathVariable Long id) {
        log.info("Recibida petición para desactivar empleado: {}", id);
        empleadoService.desactivarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleados() {
        log.info("Recibida petición para listar empleados");
        List<EmpleadoDTO> empleados = empleadoService.obtenerTodos();
        log.info("Empleados encontrados: {}", empleados.size());
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@PathVariable Long id) {
        log.info("Recibida petición para obtener empleado: {}", id);
        EmpleadoDTO empleado = empleadoService.obtenerEmpleadoPorId(id);
        return ResponseEntity.ok(empleado);
    }
}
