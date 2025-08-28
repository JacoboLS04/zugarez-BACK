package com.zugarez.zugarez_BACK.security.service;

import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.utils.Operations;
import com.zugarez.zugarez_BACK.security.dto.CreateUserDto;
import com.zugarez.zugarez_BACK.security.dto.JwtTokenDto;
import com.zugarez.zugarez_BACK.security.dto.LoginUserDto;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.enums.RoleEnum;
import com.zugarez.zugarez_BACK.security.jwt.JwtProvider;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.zugarez.zugarez_BACK.global.service.EmailService;
import com.zugarez.zugarez_BACK.global.dto.MessageDto;
import org.springframework.http.HttpStatus;
import java.util.Optional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserEntityService {
    // Verifica la contraseña usando el PasswordEncoder
    public boolean checkPassword(UserEntity user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // Genera el JWT directamente desde el usuario
    public JwtTokenDto loginByUser(UserEntity user) {
        // Crear UserPrincipal desde el usuario
        UserPrincipal userPrincipal = UserPrincipal.build(user);
        
        // Crear Authentication directamente sin validar contraseña
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userPrincipal, null, userPrincipal.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new JwtTokenDto(token, user);
    }
    @Autowired
    EmailService emailService;
    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;

    public UserEntity create(CreateUserDto dto) throws AttributeException {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty())
            throw new AttributeException("El nombre de usuario es obligatorio");
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new AttributeException("El email es obligatorio");
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty())
            throw new AttributeException("La contraseña es obligatoria");
        if (userEntityRepository.existsByUsername(dto.getUsername()))
            throw new AttributeException("El nombre de usuario ya está en uso");
        if (userEntityRepository.existsByEmail(dto.getEmail()))
            throw new AttributeException("El email ya está en uso");
        if (dto.getRoles().isEmpty())
            throw new AttributeException("Debe asignar al menos un rol al usuario");
        UserEntity user = mapUserFromDto(dto);
        // Los usuarios creados con /create están verificados por defecto
        user.setVerified(true);
        user.setVerificationToken(null);
        return userEntityRepository.save(user);
    }

    public UserEntity createAdmin(CreateUserDto dto) throws AttributeException {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty())
            throw new AttributeException("El nombre de usuario es obligatorio");
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new AttributeException("El email es obligatorio");
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty())
            throw new AttributeException("La contraseña es obligatoria");
        if (userEntityRepository.existsByUsername(dto.getUsername()))
            throw new AttributeException("El nombre de usuario ya está en uso");
        if (userEntityRepository.existsByEmail(dto.getEmail()))
            throw new AttributeException("El email ya está en uso");
        List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER");
        dto.setRoles(roles);
        UserEntity user = mapUserFromDto(dto);
        // Los administradores están verificados por defecto
        user.setVerified(true);
        user.setVerificationToken(null);
        return userEntityRepository.save(user);
    }

    public UserEntity createUser(CreateUserDto dto) throws AttributeException {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty())
            throw new AttributeException("El nombre de usuario es obligatorio");
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty())
            throw new AttributeException("El email es obligatorio");
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty())
            throw new AttributeException("La contraseña es obligatoria");
        if (userEntityRepository.existsByUsername(dto.getUsername()))
            throw new AttributeException("El nombre de usuario ya está en uso");
        if (userEntityRepository.existsByEmail(dto.getEmail()))
            throw new AttributeException("El email ya está en uso");
        List<String> roles = Arrays.asList("ROLE_USER");
        dto.setRoles(roles);
        UserEntity user = mapUserFromDto(dto);
        user.setVerified(false);
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userEntityRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);
        return user;
    }

    public JwtTokenDto login(LoginUserDto dto){
        System.out.println("=== INTENTO DE LOGIN ===");
        System.out.println("Username: " + dto.getUsername());
        System.out.println("Password recibida: " + dto.getPassword());
        boolean userExists = userEntityRepository.existsByUsername(dto.getUsername());
        System.out.println("Usuario existe en BD: " + userExists);
        UserEntity user = null;
        if (userExists) {
            user = userEntityRepository.findByUsername(dto.getUsername()).orElse(null);
            if (user != null) {
                if (!user.isVerified()) {
                    throw new RuntimeException("Usuario no verificado. Revisa tu correo electrónico.");
                }
                System.out.println("Usuario encontrado - ID: " + user.getId());
                System.out.println("Username en BD: " + user.getUsername());
                System.out.println("Password encriptada en BD: " + user.getPassword());
                System.out.println("Roles: " + user.getRoles());
            }
        }
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateToken(authentication);
            System.out.println("Login exitoso - Token generado: " + token.substring(0, 20) + "...");
            JwtTokenDto response = new JwtTokenDto(token, user);
            return response;
        } catch (Exception e) {
            System.out.println("Error en autenticación: " + e.getMessage());
            throw e;
        }
    }


    private UserEntity mapUserFromDto(CreateUserDto dto){
        String password = passwordEncoder.encode(dto.getPassword());
        List<RoleEnum> roles =
            dto.getRoles().stream().map(rol -> RoleEnum.valueOf(rol)).collect(Collectors.toList());
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(password);
        user.setRoles(roles);
        return user;
    }

    @Transactional
    public MessageDto verifyUser(String token) {
        System.out.println("[VERIFY SERVICE] Buscando token: " + token);
        Optional<UserEntity> userOpt = userEntityRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            System.out.println("[VERIFY SERVICE] Token no encontrado");
            return new MessageDto(HttpStatus.BAD_REQUEST, "Token inválido");
        }
        UserEntity user = userOpt.get();
        System.out.println("[VERIFY SERVICE] Usuario encontrado: " + user.getUsername());
        System.out.println("[VERIFY SERVICE] Estado verificado antes: " + user.isVerified());
        if (user.isVerified()) {
            return new MessageDto(HttpStatus.OK, "Usuario ya verificado");
        }
        user.setVerified(true);
        user.setVerificationToken(null);
        UserEntity savedUser = userEntityRepository.save(user);
        System.out.println("[VERIFY SERVICE] Estado verificado después: " + savedUser.isVerified());
        return new MessageDto(HttpStatus.OK, "Usuario verificado correctamente");
    }
}

