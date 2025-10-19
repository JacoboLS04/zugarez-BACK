package com.zugarez.zugarez_BACK.security.jwt;

import com.zugarez.zugarez_BACK.security.service.UserPrincipal;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Component for generating, parsing, and validating JWT tokens.
 * Handles token creation, extraction of username, and validation logic.
 */
@Component
public class JwtProvider {

    // use this class for logger to avoid accidental reference to another class
    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    /**
     * Generates a JWT token for the authenticated user.
     * Adds userId claim so we can extract it later.
     * @param authentication the authentication object
     * @return the generated JWT token
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .signWith(getKey(secret))
                .setSubject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getId()) // <-- agrega claim userId
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration * 1000))
                .claim("roles", getRoles(userPrincipal))
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     * @param token the JWT token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts the userId claim (if present) from the JWT token.
     * @param token the JWT token
     * @return userId or null if not present / parsable
     */
    public Integer getUserIdFromToken(String token) {
        try {
            Object claim = Jwts.parserBuilder()
                    .setSigningKey(getKey(secret))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId");
            if (claim == null) return null;
            if (claim instanceof Integer) return (Integer) claim;
            if (claim instanceof Long) return ((Long) claim).intValue();
            if (claim instanceof String) return Integer.parseInt((String) claim);
        } catch (Exception e) {
            logger.warn("No se pudo extraer userId del token: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Validates a JWT token.
     * @param token the JWT token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        System.out.println("Validando token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey(secret))
                    .build()
                    .parseClaimsJws(token);
            System.out.println("Token válido");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expirado: " + e.getMessage());
            logger.error("Token expirado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Token mal formado: " + e.getMessage());
            logger.error("Token mal formado: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Error con la firma del token: " + e.getMessage());
            logger.error("Error con la firma del token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Token vacío o nulo: " + e.getMessage());
            logger.error("Token vacío o nulo: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error al validar el token: " + e.getMessage());
            logger.error("Error al validar el token: " + e.getMessage());
        }
        System.out.println("Token inválido");
        return false;
    }

    private List<String> getRoles(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    private Key getKey(String secret) {
        if (secret == null) {
            logger.error("JWT secret is null");
            throw new IllegalStateException("JWT secret not configured");
        }
        try {
            // Prefer base64-decoded secret (common with jjwt examples)
            byte[] secretBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(secretBytes);
        } catch (IllegalArgumentException ex) {
            // Fallback: maybe secret is raw bytes (not base64) -> use UTF-8 bytes
            logger.warn("JWT secret is not valid base64, falling back to raw bytes: {}", ex.getMessage());
            byte[] raw = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(raw);
        }
    }
}