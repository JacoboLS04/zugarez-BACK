package com.zugarez.zugarez_BACK.security.service;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of UserDetailsService for Spring Security authentication.
 * Loads user details from the database using UserEntityRepository.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserEntityRepository userEntityRepository;

    /**
     * Loads a user by username for authentication.
     * @param username the username to search for
     * @return UserDetails for authentication
     * @throws UsernameNotFoundException if the user is not found or is deactivated
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("=== CARGANDO USUARIO PARA AUTENTICACIÓN ===");
        System.out.println("Buscando usuario: " + username);
        
        Optional<UserEntity> userEntity = userEntityRepository.findByUsername(username);
        
        if(!userEntity.isPresent()) {
            System.out.println("Usuario no encontrado en BD");
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        UserEntity user = userEntity.get();
        
        // NUEVO: Bloquear usuarios desactivados (baja voluntaria)
        if (user.getDeactivatedAt() != null) {
            System.out.println("Usuario desactivado - Fecha: " + user.getDeactivatedAt());
            throw new UsernameNotFoundException("Usuario desactivado");
        }
        
        System.out.println("Usuario cargado - ID: " + user.getId() + ", Username: " + user.getUsername());
        System.out.println("Password encriptada: " + user.getPassword());
        System.out.println("Roles: " + user.getRoles());

        return UserPrincipal.build(user);
    }
}
