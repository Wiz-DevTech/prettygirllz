package com.wizdevtech.identityaccess.service;

import java.util.Base64;
import java.security.SecureRandom;

public class KeyGenerator {
    public static void main(String[] args) {
        // Generate 32 random bytes
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        // Encode to Base64
        String encodedKey = Base64.getEncoder().encodeToString(key);
        System.out.println(encodedKey);
    }
}