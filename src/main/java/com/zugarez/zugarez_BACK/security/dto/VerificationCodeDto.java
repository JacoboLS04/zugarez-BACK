package com.zugarez.zugarez_BACK.security.dto;

public class VerificationCodeDto {
    private String email;
    private String username;
    private String code;

    public VerificationCodeDto() {}

    public VerificationCodeDto(String email, String username, String code) {
        this.email = email;
        this.username = username;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}