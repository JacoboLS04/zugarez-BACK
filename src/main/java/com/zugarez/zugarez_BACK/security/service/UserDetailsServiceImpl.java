package com.zugarez.zugarez_BACK.security.service;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import com.zugarez.zugarez_BACK.security.repository.UserEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of Spring Security's UserDetailsService interface.
 * Loads user-specific data for authentication by username.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserEntityRepository userEntityRepository;

    /**
     * Loads a user by username for authentication purposes.
     * @param username the username to search for
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Cargando usuario para autenticación: {}", username);
        
        Optional<UserEntity> userEntity = userEntityRepository.findByUsername(username);
        
        if(!userEntity.isPresent()) {
            logger.error("Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        UserEntity user = userEntity.get();
        logger.info("Usuario cargado: {} (ID: {})", user.getUsername(), user.getId());

        return UserPrincipal.build(user);
    }
}
