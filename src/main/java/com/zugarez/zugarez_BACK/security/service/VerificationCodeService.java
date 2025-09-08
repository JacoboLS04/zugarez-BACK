package com.zugarez.zugarez_BACK.security.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Map;

/**
 * Service for generating and verifying short verification codes for user actions.
 * Stores codes in memory mapped to an identifier (e.g., email or username).
 */
@Service
public class VerificationCodeService {
    private final Map<String, Integer> codeMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    /**
     * Generates a two-digit verification code for the given identifier and stores it.
     * @param identifier the identifier to associate with the code (e.g., email)
     * @return the generated code
     */
    public int generateCode(String identifier) {
        int code = 10 + random.nextInt(90); // Código de dos dígitos (10-99)
        codeMap.put(identifier, code);
        System.out.println("[VerificationCodeService] Código generado para " + identifier + ": " + code);
        System.out.println("[VerificationCodeService] Códigos almacenados: " + codeMap);
        return code;
    }

    /**
     * Verifies if the provided code matches the stored code for the identifier.
     * Removes the code if verification is successful.
     * @param identifier the identifier to check
     * @param code the code to verify
     * @return true if the code is correct, false otherwise
     */
    public boolean verifyCode(String identifier, int code) {
        System.out.println("[VerificationCodeService] Verificando código para " + identifier + ": " + code);
        System.out.println("[VerificationCodeService] Códigos almacenados antes: " + codeMap);
        
        Integer stored = codeMap.get(identifier);
        System.out.println("[VerificationCodeService] Código almacenado: " + stored);
        
        if (stored != null && stored == code) {
            codeMap.remove(identifier); // Solo remover si el código es correcto
            System.out.println("[VerificationCodeService] Código correcto, eliminado del mapa");
            return true;
        }
        System.out.println("[VerificationCodeService] Código incorrecto o no encontrado");
        return false;
    }
}