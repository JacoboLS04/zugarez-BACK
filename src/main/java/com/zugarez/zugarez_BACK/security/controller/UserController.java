package com.zugarez.zugarez_BACK.security.controller;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.enums.RoleEnum;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoints protegidos para perfil y baja voluntaria.
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserEntityRepository userRepository;

    public static class UnsubscribeRequest {
        public String name;
        public String email;
        public String reason;
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Object o = request.getAttribute("authenticatedUser");
        if (!(o instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","No autorizado"));
        }
        UserEntity user = (UserEntity) o;
        Map<String,Object> resp = new HashMap<>();
        resp.put("id", user.getId());
        resp.put("name", user.getUsername());
        resp.put("email", user.getEmail());
        resp.put("verified", user.isVerified());
        resp.put("createdAt", null);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(HttpServletRequest request, @RequestBody UnsubscribeRequest body) {
        System.out.println("=== ENDPOINT /api/unsubscribe LLAMADO ===");
        System.out.println("Name: " + body.name);
        System.out.println("Email: " + body.email);
        System.out.println("Reason: " + body.reason);
        
        Object o = request.getAttribute("authenticatedUser");
        if (!(o instanceof UserEntity)) {
            System.out.println("ERROR: No hay usuario autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","No autorizado"));
        }
        UserEntity user = (UserEntity) o;
        
        System.out.println("Usuario autenticado ID: " + user.getId() + ", Username: " + user.getUsername());

        // Prohibir que administradores se auto-den de baja
        if (user.getRoles() != null && user.getRoles().stream().anyMatch(r -> r == RoleEnum.ROLE_ADMIN)) {
            System.out.println("ERROR: Usuario es administrador");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error","Los administradores no pueden darse de baja desde esta ruta"));
        }

        // Validaciones
        String name = body.name == null ? "" : body.name.trim();
        String reason = body.reason == null ? "" : body.reason.trim();

        if (name.isEmpty()) {
            name = user.getUsername() != null ? user.getUsername().trim() : "";
        }

        Map<String,String> details = new HashMap<>();
        if (name.isEmpty()) details.put("name","El nombre no puede estar vacío");
        if (reason.length() < 10) details.put("reason","El motivo debe tener al menos 10 caracteres");
        if (!details.isEmpty()) {
            System.out.println("ERROR: Validaciones fallidas - " + details);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Datos inválidos","details", details));
        }

        // Verificar si ya está dado de baja
        if (user.getDeactivatedAt() != null) {
            System.out.println("ERROR: Usuario ya desactivado - Fecha: " + user.getDeactivatedAt());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","El usuario ya está dado de baja"));
        }

        // ⚠️ IMPORTANTE: NO CAMBIAR 'verified' - solo establecer deactivatedAt
        LocalDateTime now = LocalDateTime.now();
        user.setDeactivationReason(reason);
        user.setDeactivatedAt(now);
        
        System.out.println("=== GUARDANDO USUARIO DESACTIVADO ===");
        System.out.println("DeactivatedAt: " + now);
        System.out.println("DeactivationReason: " + reason);
        System.out.println("Verified (no cambia): " + user.isVerified());
        
        UserEntity saved = userRepository.save(user);
        
        System.out.println("=== USUARIO GUARDADO ===");
        System.out.println("DeactivatedAt guardado: " + saved.getDeactivatedAt());
        System.out.println("Verified guardado: " + saved.isVerified());

        Map<String,Object> resp = new HashMap<>();
        resp.put("message", "Solicitud de baja procesada correctamente");
        resp.put("deactivatedAt", saved.getDeactivatedAt());
        return ResponseEntity.ok(resp);
    }
}
