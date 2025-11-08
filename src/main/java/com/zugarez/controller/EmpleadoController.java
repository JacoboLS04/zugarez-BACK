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
        log.info("=== INICIANDO CREACIÓN DE EMPLEADO ===");
        log.info("Datos recibidos: {}", dto);
        try {
            EmpleadoDTO empleado = empleadoService.crearEmpleado(dto);
            log.info("Empleado creado exitosamente con ID: {}", empleado.getId());
            return ResponseEntity.ok(empleado);
        } catch (Exception e) {
            log.error("ERROR AL CREAR EMPLEADO: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        log.info("Actualizando empleado con ID: {}", id);
        EmpleadoDTO empleado = empleadoService.actualizarEmpleado(id, dto);
        return ResponseEntity.ok(empleado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarEmpleado(@PathVariable Long id) {
        log.info("Desactivando empleado con ID: {}", id);
        empleadoService.desactivarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleados() {
        log.info("=== LISTANDO EMPLEADOS ===");
        List<EmpleadoDTO> empleados = empleadoService.obtenerTodos();
        log.info("Total empleados encontrados: {}", empleados.size());
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@PathVariable Long id) {
        log.info("Obteniendo empleado con ID: {}", id);
        EmpleadoDTO empleado = empleadoService.obtenerEmpleadoPorId(id);
        return ResponseEntity.ok(empleado);
    }
}
