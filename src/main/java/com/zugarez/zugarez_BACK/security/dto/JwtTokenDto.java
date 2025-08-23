package com.zugarez.zugarez_BACK.security.dto;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;

public class JwtTokenDto {
    private String token;
    private UserEntity user;

    public JwtTokenDto() {
    }

    public JwtTokenDto(String token) {
        this.token = token;
    }

    public JwtTokenDto(String token, UserEntity user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
