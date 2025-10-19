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
 * El AuthInterceptor añade el atributo "authenticatedUser" a la request.
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
        // Si tienes createdAt en la entidad, reemplaza null por user.getCreatedAt()
        resp.put("createdAt", null);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(HttpServletRequest request, @RequestBody UnsubscribeRequest body) {
        Object o = request.getAttribute("authenticatedUser");
        if (!(o instanceof UserEntity)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","No autorizado"));
        }
        UserEntity user = (UserEntity) o;

        // Prohibir que administradores se auto-den de baja desde este endpoint
        if (user.getRoles() != null && user.getRoles().stream().anyMatch(r -> r == RoleEnum.ROLE_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error","Los administradores no pueden darse de baja desde esta ruta"));
        }

        // Sanitizar y usar valores mínimos; email viene del usuario autenticado
        String name = body.name == null ? "" : body.name.trim();
        String reason = body.reason == null ? "" : body.reason.trim();

        if (name.isEmpty()) {
            name = user.getUsername() != null ? user.getUsername().trim() : "";
        }

        Map<String,String> details = new HashMap<>();
        if (name.isEmpty()) details.put("name","El nombre no puede estar vacío");
        if (reason.length() < 10) details.put("reason","El motivo debe tener al menos 10 caracteres");
        if (!details.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Datos inválidos","details", details));
        }

        // Validar estado actual: si ya estaba dado de baja -> 409
        if (!user.isVerified() && user.getDeactivatedAt() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","El usuario ya está dado de baja"));
        }

        // Actualizar estado en la base de datos (mínimo necesario)
        user.setVerified(false);
        user.setDeactivationReason(reason);
        user.setDeactivatedAt(LocalDateTime.now());
        userRepository.save(user);

        Map<String,Object> resp = new HashMap<>();
        resp.put("message", "Solicitud de baja procesada correctamente");
        resp.put("deactivatedAt", user.getDeactivatedAt());
        return ResponseEntity.ok(resp);
    }
}
