package com.zugarez.zugarez_BACK.security.controller;
import com.zugarez.zugarez_BACK.global.dto.MessageDto;
import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.service.TemplateService;
import com.zugarez.zugarez_BACK.security.dto.CreateUserDto;
import com.zugarez.zugarez_BACK.security.dto.JwtTokenDto;
import com.zugarez.zugarez_BACK.security.dto.LoginUserDto;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.service.UserEntityService;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

/**
 * REST controller for authentication and user verification endpoints.
 * Handles user login, registration, and verification code logic.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    UserEntityService userEntityService;
    @Autowired
    UserEntityRepository userEntityRepository;
    @Autowired
    TemplateService templateService;
    @Autowired
    com.zugarez.zugarez_BACK.global.service.EmailService emailService;

    /**
     * Verifies a code for a user by email or username.
     * @param dto VerificationCodeDto containing email/username and code
     * @return ResponseEntity with verification result
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody com.zugarez.zugarez_BACK.security.dto.VerificationCodeDto dto) {
        System.out.println("=== MÉTODO /verify-code EJECUTÁNDOSE ===");
        System.out.println("Email recibido: " + dto.getEmail());
        System.out.println("Code recibido: " + dto.getCode());
        
        Optional<UserEntity> userOpt = Optional.empty();
        
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            userOpt = userEntityRepository.findByEmail(dto.getEmail());
        }
        if (userOpt.isEmpty() && dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            userOpt = userEntityRepository.findByUsername(dto.getUsername());
        }
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        }
        
        UserEntity user = userOpt.get();
        
        System.out.println("=== VERIFICANDO ESTADO DEL USUARIO ===");
        System.out.println("Usuario ID: " + user.getId());
        System.out.println("DeactivatedAt: " + user.getDeactivatedAt());
        System.out.println("Verified: " + user.isVerified());
        
        // BLOQUEAR usuarios desactivados (baja voluntaria)
        if (user.getDeactivatedAt() != null) {
            System.out.println("ERROR: Usuario desactivado, bloqueando login");
            Map<String,Object> body = new HashMap<>();
            body.put("error", "Tu cuenta ha sido desactivada");
            body.put("message", "Tu solicitud de baja fue procesada. Contacta soporte si deseas reactivar tu cuenta.");
            body.put("deactivatedAt", user.getDeactivatedAt());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
        
        // Verificar código de login
        if (user.getLoginCode() == null || !user.getLoginCode().equals(dto.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Código incorrecto"));
        }
        
        // Verificar expiración
        if (user.getLoginCodeExpiry() == null || LocalDateTime.now().isAfter(user.getLoginCodeExpiry())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Código expirado"));
        }
        
        // Código válido, limpiar y generar JWT
        user.setLoginCode(null);
        user.setLoginCodeExpiry(null);
        userEntityRepository.save(user);
        
        JwtTokenDto jwtTokenDto = userEntityService.loginByUser(user);
        return ResponseEntity.ok(jwtTokenDto);
    }

    @PostMapping("/create")
    public ResponseEntity<MessageDto> create(@Valid @RequestBody CreateUserDto dto) throws AttributeException {
        UserEntity userEntity = userEntityService.create(dto);
        return ResponseEntity.ok(
                new MessageDto(HttpStatus.OK,"usuario " + userEntity.getUsername() + " creado correctamente")
        );
    }

    @PostMapping("/create-admin")
    public ResponseEntity<MessageDto> createAdmin(@Valid @RequestBody CreateUserDto dto) throws AttributeException {
        UserEntity userEntity = userEntityService.createAdmin(dto);
        return ResponseEntity.ok(
                new MessageDto(HttpStatus.OK,"admin " + userEntity.getUsername() + " creado correctamente")
        );
    }

    @PostMapping("/create-user")
    public ResponseEntity<MessageDto> createUser(@Valid @RequestBody CreateUserDto dto) throws AttributeException {
        UserEntity userEntity = userEntityService.createUser(dto);
        return ResponseEntity.ok(
                new MessageDto(HttpStatus.OK,"user " + userEntity.getUsername() + " creado correctamente")
        );
    }

    // Paso 1: Login con envío de código
    @PostMapping("/login/init")
    public ResponseEntity<MessageDto> loginInit(@Valid @RequestBody LoginUserDto dto) {
        System.out.println("=== LOGIN INIT ===");
        System.out.println("Email/Username recibido: " + dto.getEmail());
        
        Optional<UserEntity> userOpt = Optional.empty();
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            userOpt = userEntityRepository.findByEmail(dto.getEmail()); 
        }
        if (userOpt.isEmpty() && dto.getUsername() != null && !dto.getUsername().isEmpty()) {
            userOpt = userEntityRepository.findByUsername(dto.getUsername());
        }
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));
        }
        
        UserEntity user = userOpt.get();
        if (!userEntityService.checkPassword(user, dto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));
        }
        
        // Generar código de 2 dígitos (10-99)
        Random random = new Random();
        int code = random.nextInt(90) + 10;
        String codeStr = String.valueOf(code);
        
        // Establecer expiración a 5 minutos
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        
        // Guardar código en la base de datos
        user.setLoginCode(codeStr);
        user.setLoginCodeExpiry(expiry);
        userEntityRepository.save(user);
        
        System.out.println("Código generado: " + codeStr);
        System.out.println("Expira en: " + expiry);
        
        // Enviar email
        emailService.sendLoginCodeEmail(user.getEmail(), code);
        
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, "Código enviado al correo"));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        System.out.println("[VERIFY] Token recibido: " + token);
        MessageDto result = userEntityService.verifyUser(token);
        System.out.println("[VERIFY] Resultado: " + result.getMessage());
        
        String htmlResponse;
        if (result.getStatus() == HttpStatus.OK) {
            if (result.getMessage().contains("ya verificado")) {
                htmlResponse = templateService.getVerificationAlreadyPage();
            } else {
                htmlResponse = templateService.getVerificationSuccessPage();
            }
        } else {
            htmlResponse = templateService.getVerificationErrorPage();
        }
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/html; charset=UTF-8")
            .body(htmlResponse);
    }

    // Endpoint temporal para verificar usuario manualmente
    @PostMapping("/manual-verify/{username}")
    public ResponseEntity<MessageDto> manualVerify(@PathVariable String username) {
        Optional<UserEntity> userOpt = userEntityRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageDto(HttpStatus.BAD_REQUEST, "Usuario no encontrado"));
        }
        UserEntity user = userOpt.get();
        user.setVerified(true);
        user.setVerificationToken(null);
        userEntityRepository.save(user);
        return ResponseEntity.ok(new MessageDto(HttpStatus.OK, "Usuario verificado manualmente"));
    }
}
