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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
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
        logger.info("Verificando código para email/username: {}", dto.getEmail());
        
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
        
        // BLOQUEAR usuarios desactivados
        if (user.getDeactivatedAt() != null) {
            logger.warn("Usuario desactivado intentando login: {}", user.getUsername());
            Map<String,Object> body = new HashMap<>();
            body.put("error", "Tu cuenta ha sido desactivada");
            body.put("message", "Tu solicitud de baja fue procesada. Contacta soporte si deseas reactivar tu cuenta.");
            body.put("deactivatedAt", user.getDeactivatedAt());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
        
        // Verificar código
        if (user.getLoginCode() == null || !user.getLoginCode().equals(dto.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Código incorrecto"));
        }
        
        if (user.getLoginCodeExpiry() == null || LocalDateTime.now().isAfter(user.getLoginCodeExpiry())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Código expirado"));
        }
        
        // Limpiar código y generar JWT
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
        logger.info("Iniciando login para: {}", dto.getEmail());
        
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
        
        // BLOQUEAR usuarios desactivados antes de validar password
        if (user.getDeactivatedAt() != null) {
            logger.warn("Usuario desactivado intentando login: {}", user.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageDto(HttpStatus.FORBIDDEN, "Tu cuenta ha sido desactivada. Contacta soporte para reactivarla."));
        }
        
        if (!userEntityService.checkPassword(user, dto.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageDto(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos"));
        }
        
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Código de 6 dígitos (100000-999999)
        String codeStr = String.valueOf(code);
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        
        user.setLoginCode(codeStr);
        user.setLoginCodeExpiry(expiry);
        userEntityRepository.save(user);
        
        logger.info("Código de 6 dígitos generado para {}: {}", user.getUsername(), codeStr);
        
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
