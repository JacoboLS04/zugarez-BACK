package com.zugarez.zugarez_BACK.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for user creation requests.
 * Contains username, email, password, and roles fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    /** Username for the new user (required) */
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;
    /** Email for the new user (required, must be valid) */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    /** Password for the new user (required) */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    /** List of roles assigned to the new user */
    List<String> roles = new ArrayList<>();

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

    /**
     * Gets the list of roles assigned to the user.
     * @return List of roles
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles assigned to the user.
     * @param roles List of roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
