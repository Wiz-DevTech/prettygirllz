package com.wizdevtech.identityaccess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // 16 bytes
    private static final int IV_LENGTH_BYTE = 12; // 96 bits - recommended for GCM

    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptionService(@Value("${encryption.key}") String key) {
        try {
            // Create a fixed-length key that works with AES
            byte[] keyBytes = new byte[32]; // AES-256 requires a 32-byte key

            // Get the bytes from the string
            byte[] inputBytes = key.getBytes(StandardCharsets.UTF_8);

            // Copy as many bytes as we can from the input (up to 32)
            System.arraycopy(inputBytes, 0, keyBytes, 0,
                    Math.min(inputBytes.length, keyBytes.length));

            // Create the secret key specification
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid encryption key", e);
        }
    }

    public String encrypt(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("Plaintext cannot be null");
        }

        // Handle empty strings by returning them as-is
        if (plaintext.isEmpty()) {
            return plaintext;
        }

        try {
            // Generate IV
            byte[] iv = new byte[IV_LENGTH_BYTE];
            secureRandom.nextBytes(iv);

            // Initialize cipher
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            // Encrypt
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Combine IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);

            // Base64 encode for storage
            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new IllegalArgumentException("Encryption failed", e);
        }
    }

    public String decrypt(String base64Ciphertext) {
        if (base64Ciphertext == null) {
            throw new IllegalArgumentException("Ciphertext cannot be null");
        }

        // Handle empty strings by returning them as-is
        if (base64Ciphertext.isEmpty()) {
            return base64Ciphertext;
        }

        try {
            // Use standard decoder for decryption
            byte[] decoded = Base64.getDecoder().decode(base64Ciphertext);

            // Validate minimum length
            if (decoded.length < IV_LENGTH_BYTE + TAG_LENGTH_BIT/8) {
                throw new IllegalArgumentException("Invalid ciphertext length");
            }

            // Extract IV
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);

            // Extract encrypted data
            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);

            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            // Decrypt
            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cipher text", e);
        }
    }

    // Keep the exception class for other potential uses
    public static class EncryptionException extends RuntimeException {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}