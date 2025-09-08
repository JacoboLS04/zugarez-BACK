package com.zugarez.zugarez_BACK.security.service;

import com.zugarez.zugarez_BACK.security.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of Spring Security's UserDetails interface for authentication.
 * Wraps user credentials and authorities for security context.
 */
public class UserPrincipal implements UserDetails {
    
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Default constructor.
     */
    public UserPrincipal() {
    }

    /**
     * Returns the authorities granted to the user.
     * @return collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Constructs a UserPrincipal with username, password, and authorities.
     * @param username the username
     * @param password the password
     * @param authorities the granted authorities
     */
    public UserPrincipal(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Builds a UserPrincipal from a UserEntity.
     * @param userEntity the user entity
     * @return UserPrincipal instance
     */
    public static UserPrincipal build(UserEntity userEntity) {
        Collection<GrantedAuthority> authorities = userEntity.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.name())).collect(Collectors.toList());
    return new UserPrincipal(userEntity.getUsername(), userEntity.getPassword(), authorities);
    }

    /**
     * Indicates whether the user's account has expired.
     * @return true if the account is non-expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Returns the username used to authenticate the user.
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password used to authenticate the user.
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * @return true if enabled
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     * @return true if credentials are non-expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user's account is locked.
     * @return true if the account is non-locked
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

}
