package com.zugarez.zugarez_BACK.security.service;

import com.zugarez.zugarez_BACK.global.exceptions.AttributeException;
import com.zugarez.zugarez_BACK.security.dto.CreateUserDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserEntityService.
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

    @InjectMocks
    private UserEntityService userEntityService;

    private CreateUserDto createUserDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto();
        createUserDto.setUsername("testuser");
        createUserDto.setEmail("test@example.com");
        createUserDto.setPassword("password123");
        createUserDto.setRoles(Arrays.asList("ROLE_USER"));

        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setRoles(Arrays.asList(RoleEnum.ROLE_USER));
        userEntity.setVerified(true);
    }

    @Test
    void testCreateUser_Success() throws AttributeException {
        when(userEntityRepository.existsByUsername(anyString())).thenReturn(false);
        when(userEntityRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = userEntityService.create(createUserDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.isVerified());
        verify(userEntityRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userEntityRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmailExists() {
        when(userEntityRepository.existsByUsername(anyString())).thenReturn(false);
        when(userEntityRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmptyUsername() {
        createUserDto.setUsername("");

        assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmptyEmail() {
        createUserDto.setEmail("");

        assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateUser_EmptyPassword() {
        createUserDto.setPassword("");

        assertThrows(AttributeException.class, () -> {
            userEntityService.create(createUserDto);
        });
        verify(userEntityRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testCreateAdmin_Success() throws AttributeException {
        when(userEntityRepository.existsByUsername(anyString())).thenReturn(false);
        when(userEntityRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = userEntityService.createAdmin(createUserDto);

        assertNotNull(result);
        assertTrue(result.isVerified());
        verify(userEntityRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testCheckPassword_Success() {
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        boolean result = userEntityService.checkPassword(userEntity, "password123");

        assertTrue(result);
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void testCheckPassword_Failure() {
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        boolean result = userEntityService.checkPassword(userEntity, "wrongpassword");

        assertFalse(result);
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword");
    }
}

