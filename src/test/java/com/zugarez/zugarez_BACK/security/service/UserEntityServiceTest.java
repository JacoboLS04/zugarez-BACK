package com.zugarez.zugarez_BACK.security.service;

import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.global.service.EmailService;
import com.zugarez.zugarez_BACK.security.dto.CreateUserDto;
import com.zugarez.zugarez_BACK.security.dto.JwtTokenDto;
import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.enums.RoleEnum;
import com.zugarez.zugarez_BACK.security.jwt.JwtProvider;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserEntityService.
 * Tests user authentication, registration, and password validation logic.
 */
@ExtendWith(MockitoExtension.class)
class UserEntityServiceTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEntityService userEntityService;

    private UserEntity testUser;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Collections.singletonList(RoleEnum.ROLE_USER));
        testUser.setVerified(true);

        createUserDto = new CreateUserDto();
        createUserDto.setUsername("newuser");
        createUserDto.setEmail("newuser@test.com");
        createUserDto.setPassword("password123");
        createUserDto.setRoles(Collections.singletonList("ROLE_USER"));
    }

    @Test
    void checkPassword_WhenPasswordMatches_ShouldReturnTrue() {
        // Arrange
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        // Act
        boolean result = userEntityService.checkPassword(testUser, "rawPassword");

        // Assert
        assertTrue(result);
        verify(passwordEncoder, times(1)).matches("rawPassword", "encodedPassword");
    }

    @Test
    void checkPassword_WhenPasswordDoesNotMatch_ShouldReturnFalse() {
        // Arrange
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act
        boolean result = userEntityService.checkPassword(testUser, "wrongPassword");

        // Assert
        assertFalse(result);
        verify(passwordEncoder, times(1)).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void loginByUser_ShouldGenerateJwtToken() {
        // Arrange
        when(jwtProvider.generateToken(any(Authentication.class))).thenReturn("jwt-token-123");

        // Act
        JwtTokenDto result = userEntityService.loginByUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals("jwt-token-123", result.getToken());
        assertNotNull(result.getUser());
        assertEquals("testuser", result.getUser().getUsername());
        verify(jwtProvider, times(1)).generateToken(any(Authentication.class));
    }

    @Test
    void create_WithValidData_ShouldCreateUser() throws AttributeException {
        // Arrange
        when(userEntityRepository.existsByUsername("newuser")).thenReturn(false);
        when(userEntityRepository.existsByEmail("newuser@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(userEntityRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(1);
            return user;
        });

        // Act
        UserEntity result = userEntityService.create(createUserDto);

        // Assert
        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("newuser@test.com", result.getEmail());
        assertTrue(result.isVerified());
        verify(userEntityRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void create_WithEmptyUsername_ShouldThrowException() {
        // Arrange
        createUserDto.setUsername("");

        // Act & Assert
        AttributeException exception = assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        assertEquals("El nombre de usuario es obligatorio", exception.getMessage());
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void create_WithEmptyEmail_ShouldThrowException() {
        // Arrange
        createUserDto.setEmail("");

        // Act & Assert
        AttributeException exception = assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        assertEquals("El email es obligatorio", exception.getMessage());
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void create_WithEmptyPassword_ShouldThrowException() {
        // Arrange
        createUserDto.setPassword("");

        // Act & Assert
        AttributeException exception = assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        assertTrue(exception.getMessage().contains("contraseña") ||
                   exception.getMessage().contains("password"));
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void create_WithExistingUsername_ShouldThrowException() {
        // Arrange
        when(userEntityRepository.existsByUsername("newuser")).thenReturn(true);

        // Act & Assert
        AttributeException exception = assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        assertTrue(exception.getMessage().contains("nombre de usuario") ||
                   exception.getMessage().contains("username"));
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void create_WithExistingEmail_ShouldThrowException() {
        // Arrange
        when(userEntityRepository.existsByUsername("newuser")).thenReturn(false);
        when(userEntityRepository.existsByEmail("newuser@test.com")).thenReturn(true);

        // Act & Assert
        AttributeException exception = assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        assertTrue(exception.getMessage().contains("email") ||
                   exception.getMessage().contains("correo"));
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }
}
