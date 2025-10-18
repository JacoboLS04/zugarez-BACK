package com.zugarez.zugarez_BACK.security.dto;

/**
 * Data Transfer Object for verification code operations (e.g., email verification).
 * Contains email, username, and verification code fields.
 */
public class VerificationCodeDto {
    /** Email address associated with the verification */
    private String email;
    /** Username associated with the verification */
    private String username;
    /** Verification code */
    private String code;

    /**
     * Default constructor.
     */
    public VerificationCodeDto() {}

    /**
     * Full constructor for VerificationCodeDto.
     * @param email Email address
     * @param username Username
     * @param code Verification code
     */
    public VerificationCodeDto(String email, String username, String code) {
        this.email = email;
        this.username = username;
        this.code = code;
    }

    /**
     * Gets the email address.
     * @return Email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     * @param email Email address
     */
    public void setEmail(String email) {
        this.email = email;
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
     * Gets the verification code.
     * @return Verification code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the verification code.
     * @param code Verification code
     */
    public void setCode(String code) {
        this.code = code;
    }
}