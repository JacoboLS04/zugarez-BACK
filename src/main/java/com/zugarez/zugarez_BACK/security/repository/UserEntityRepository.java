package com.zugarez.zugarez_BACK.security.repository;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserEntity persistence operations.
 * Provides methods to find users by username, email, and verification token.
 */
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
    /**
     * Checks if a user exists by username.
     * @param username the username to check
     * @return true if a user exists with the given username
     */
    boolean existsByUsername(String username);
    /**
     * Checks if a user exists by email.
     * @param email the email to check
     * @return true if a user exists with the given email
     */
    boolean existsByEmail(String email);
    /**
     * Finds a user by username.
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByUsername(String username);
    /**
     * Finds a user by email.
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByEmail(String email);
    /**
     * Finds a user by verification token.
     * @param verificationToken the token to search for
     * @return Optional containing the user if found
     */
    Optional<UserEntity> findByVerificationToken(String verificationToken);
    
    // Nuevo método para obtener usuarios desactivados
    @Query("SELECT u FROM UserEntity u WHERE u.deactivatedAt IS NOT NULL ORDER BY u.deactivatedAt DESC")
    List<UserEntity> findDeactivatedUsers();
}
