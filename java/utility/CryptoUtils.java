package com.cfa.api.corpsupplier.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/*
partially from:
https://mkyong.com/java/java-aes-encryption-and-decryption/
 */
public class CryptoUtils {

    private CryptoUtils() {}
    public static String hashSHA3(String plaintext) {
        byte[] hashBytes = DigestUtils.sha3_256(plaintext);
        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public static String hashSHA2(String plaintext) {
        byte[] hashBytes = DigestUtils.sha256(plaintext);
        return Base64.getEncoder().encodeToString(hashBytes);
    }
    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    public static SecretKey getAESKey(int keysize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keysize, SecureRandom.getInstanceStrong());
        return keyGen.generateKey();
    }

    public static SecretKey getAESKeyFromPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public static String hexWithBlockSize(byte[] bytes, int blockSize) {
        String hex = hex(bytes);
        blockSize = blockSize * 2;
        List<String> result = new ArrayList<>();
        int index = 0;
        while (index < hex.length()) {
            result.add(hex.substring(index, Math.min(index + blockSize, hex.length())));
            index += blockSize;
        }

        return result.toString();
    }

}