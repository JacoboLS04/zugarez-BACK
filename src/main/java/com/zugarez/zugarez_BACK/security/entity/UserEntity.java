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
    @Column(name = "deactivation_reason", length = 500)
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

    /**
     * Sets the verification token.
     * @param verificationToken the verification token
     */
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    /**
     * Gets the login code.
     * @return the login code
     */
    public String getLoginCode() {
        return loginCode;
    }

    /**
     * Sets the login code.
     * @param loginCode the login code
     */
    public void setLoginCode(String loginCode) {
        this.loginCode = loginCode;
    }

    /**
     * Gets the login code expiry time.
     * @return the login code expiry time
     */
    public LocalDateTime getLoginCodeExpiry() {
        return loginCodeExpiry;
    }

    /**
     * Sets the login code expiry time.
     * @param loginCodeExpiry the login code expiry time
     */
    public void setLoginCodeExpiry(LocalDateTime loginCodeExpiry) {
        this.loginCodeExpiry = loginCodeExpiry;
    }

    /**
     * Gets the user ID.
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     * @param id the user ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the encrypted password.
     * @return the encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encrypted password.
     * @param password the encrypted password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the list of roles.
     * @return the list of roles
     */
    public List<RoleEnum> getRoles() {
        return roles;
    }

    /**
     * Sets the list of roles.
     * @param roles the list of roles
     */
    public void setRoles(List<RoleEnum> roles) {
        this.roles = roles;
    }

    /**
     * Gets the deactivation reason.
     * @return the deactivation reason
     */
    public String getDeactivationReason() {
        return deactivationReason;
    }

    /**
     * Sets the deactivation reason.
     * @param deactivationReason the deactivation reason
     */
    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    /**
     * Gets the deactivation timestamp.
     * @return the deactivation timestamp
     */
    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    /**
     * Sets the deactivation timestamp.
     * @param deactivatedAt the deactivation timestamp
     */
    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }


}
