package com.zugarez.zugarez_BACK.security.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user login requests.
 * Contains username, email, and password fields.
 */
public class LoginUserDto {
    /** Username for login */
    private String username;
    /** Email for login */
    private String email;
    /** Password for login (required) */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * Default constructor.
     */
    public LoginUserDto() {
    }

    /**
     * Full constructor for LoginUserDto.
     * @param username Username
     * @param email Email
     * @param password Password
     */
    public LoginUserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the username.
     * @return Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username Username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email.
     * @return Email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password.
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password Password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
