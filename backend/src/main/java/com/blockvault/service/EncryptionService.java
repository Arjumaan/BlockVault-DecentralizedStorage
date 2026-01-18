package com.blockvault.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
public class EncryptionService {

    @Value("${blockvault.encryption.algorithm}")
    private String algorithm;

    @Value("${blockvault.encryption.key-size}")
    private int keySize;

    private static final String CIPHER_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    /**
     * Encrypt data using AES-256-GCM
     * 
     * @param data Data to encrypt
     * @param key  Encryption key (Base64 encoded)
     * @return Encrypted data (IV + encrypted bytes)
     */
    public byte[] encrypt(byte[] data, String key) throws Exception {
        try {
            SecretKey secretKey = decodeKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);

            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(data);

            // Combine IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            log.debug("Data encrypted successfully");
            return byteBuffer.array();
        } catch (Exception e) {
            log.error("Encryption failed: {}", e.getMessage());
            throw new Exception("Encryption failed: " + e.getMessage());
        }
    }

    /**
     * Decrypt data using AES-256-GCM
     * 
     * @param encryptedData Encrypted data (IV + encrypted bytes)
     * @param key           Encryption key (Base64 encoded)
     * @return Decrypted data
     */
    public byte[] decrypt(byte[] encryptedData, String key) throws Exception {
        try {
            SecretKey secretKey = decodeKey(key);

            // Extract IV and encrypted data
            ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] decryptedData = cipher.doFinal(cipherText);

            log.debug("Data decrypted successfully");
            return decryptedData;
        } catch (Exception e) {
            log.error("Decryption failed: {}", e.getMessage());
            throw new Exception("Decryption failed: " + e.getMessage());
        }
    }

    /**
     * Generate a new AES-256 encryption key
     * 
     * @return Base64 encoded encryption key
     */
    public String generateKey() throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            keyGenerator.init(keySize);
            SecretKey secretKey = keyGenerator.generateKey();
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            log.debug("New encryption key generated");
            return encodedKey;
        } catch (Exception e) {
            log.error("Key generation failed: {}", e.getMessage());
            throw new Exception("Key generation failed: " + e.getMessage());
        }
    }

    /**
     * Hash a key for storage (don't store the actual encryption key!)
     * 
     * @param key Encryption key
     * @return SHA-256 hash of the key
     */
    public String hashKey(String key) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(key.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Key hashing failed: {}", e.getMessage());
            throw new Exception("Key hashing failed: " + e.getMessage());
        }
    }

    /**
     * Decode Base64 key to SecretKey
     */
    private SecretKey decodeKey(String encodedKey) {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, algorithm);
    }

    /**
     * Validate encryption key format
     */
    public boolean isValidKey(String key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            return decodedKey.length == (keySize / 8);
        } catch (Exception e) {
            return false;
        }
    }
}
