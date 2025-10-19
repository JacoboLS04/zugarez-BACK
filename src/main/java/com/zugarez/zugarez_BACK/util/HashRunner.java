package com.zugarez.zugarez_BACK.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Simple runner to demonstrate hashing the literal "123" with the application's PasswordEncoder bean.
 * It prints the encoded value and verifies it with matches().
 */
@Component
public class HashRunner implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public HashRunner(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        String raw = "123";
        String encoded = passwordEncoder.encode(raw);
        System.out.println("--- HashRunner demo ---");
        System.out.println("Raw   : " + raw);
        System.out.println("Encoded: " + encoded);
        System.out.println("Matches: " + passwordEncoder.matches(raw, encoded));
        System.out.println("--- End demo ---");
    }
}
