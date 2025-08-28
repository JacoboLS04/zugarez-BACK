package com.zugarez.zugarez_BACK.security.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;
import java.util.Map;

@Service
public class VerificationCodeService {
    private final Map<String, Integer> codeMap = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public int generateCode(String identifier) {
        int code = 10 + random.nextInt(90); // Código de dos dígitos (10-99)
        codeMap.put(identifier, code);
        System.out.println("[VerificationCodeService] Código generado para " + identifier + ": " + code);
        System.out.println("[VerificationCodeService] Códigos almacenados: " + codeMap);
        return code;
    }

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