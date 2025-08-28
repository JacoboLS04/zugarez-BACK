package com.zugarez.zugarez_BACK.security.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginUserDto {
    private String username;
    private String email;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public LoginUserDto() {
    }

    public LoginUserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


