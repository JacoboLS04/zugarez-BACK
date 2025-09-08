package com.zugarez.zugarez_BACK.security.dto;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;

/**
 * Data Transfer Object for JWT token responses.
 * Contains the JWT token and optionally the user entity.
 */
public class JwtTokenDto {
    /** JWT token string */
    private String token;
    /** User entity associated with the token (optional) */
    private UserEntity user;

    /**
     * Default constructor.
     */
    public JwtTokenDto() {
    }

    /**
     * Constructor with token only.
     * @param token JWT token
     */
    public JwtTokenDto(String token) {
        this.token = token;
    }

    /**
     * Constructor with token and user entity.
     * @param token JWT token
     * @param user User entity
     */
    public JwtTokenDto(String token, UserEntity user) {
        this.token = token;
        this.user = user;
    }

    /**
     * Gets the JWT token.
     * @return JWT token
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the JWT token.
     * @param token JWT token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the user entity associated with the token.
     * @return User entity
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Sets the user entity associated with the token.
     * @param user User entity
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
