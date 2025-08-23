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

    @PostMapping("/login")
    public ResponseEntity<JwtTokenDto> create(@Valid @RequestBody LoginUserDto dto) throws AttributeException {
        JwtTokenDto jwtTokenDto = userEntityService.login(dto);
        return ResponseEntity.ok(jwtTokenDto);
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
