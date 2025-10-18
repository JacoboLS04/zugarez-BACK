package com.zugarez.zugarez_BACK.security.entity;

import com.zugarez.zugarez_BACK.security.enums.RoleEnum;
import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Entity representing a user in the system.
 * Stores authentication, verification, and role information.
 */
@Entity
@Table(name = "users")
public class UserEntity {
    /** Indicates if the user is verified */
    @Column(name = "verified")
    private boolean verified = false;

    /** Token used for email verification */
    @Column(name = "verification_token")
    private String verificationToken;

    /** Code used for login verification */
    @Column(name = "login_code")
    private String loginCode;
    
    /** Expiry time for the login code */
    @Column(name = "login_code_expiry")
    private LocalDateTime loginCodeExpiry;
    /** Unique identifier for the user */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Username of the user */
    private String username;
    /** Email address of the user */
    private String email;
    /** Encrypted password of the user */
    private String password;
    /** List of roles assigned to the user */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private List<RoleEnum> roles;

    /** Motivo de desactivación (nullable) */
    @Column(name = "deactivation_reason")
    private String deactivationReason;
    /** Fecha/hora de desactivación (nullable) */
    @Column(name = "deactivated_at")
    private LocalDateTime deactivatedAt;

    /**
     * Default constructor.
     */
    public UserEntity() {
    }

    /**
     * Full constructor for UserEntity.
     * @param id user ID
     * @param username username
     * @param email email address
     * @param password encrypted password
     * @param roles list of roles
     * @param verified verification status
     * @param verificationToken email verification token
     * @param loginCode login code
     * @param loginCodeExpiry login code expiry
     */
    public UserEntity(int id, String username, String email, String password, List<RoleEnum> roles, boolean verified, String verificationToken, String loginCode, LocalDateTime loginCodeExpiry) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.verified = verified;
        this.verificationToken = verificationToken;
        this.loginCode = loginCode;
        this.loginCodeExpiry = loginCodeExpiry;
    }
    /**
     * Checks if the user is verified.
     * @return true if verified
     */
    public boolean isVerified() {
        return verified;
    }
    /**
     * Sets the verification status.
     * @param verified true if verified
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    /**
     * Gets the verification token.
     * @return verification token
     */
    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getLoginCode() {
        return loginCode;
    }

    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    public LocalDateTime getLoginCodeExpiry() {
        return loginCodeExpiry;
    }

    public void setLoginCodeExpiry(LocalDateTime loginCodeExpiry) {
        this.loginCodeExpiry = loginCodeExpiry;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<RoleEnum> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEnum> roles) {
        this.roles = roles;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }


}
