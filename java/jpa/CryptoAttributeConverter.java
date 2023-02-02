package com.cfa.api.corpsupplier.model.converter;

import com.cfa.api.corpsupplier.configuration.CryptoProperties;
import com.cfa.api.corpsupplier.util.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.persistence.AttributeConverter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/*
Usage: Annotate entity field like so
    @Convert(converter = CryptoAttributeConverter.class)
    @Column(name = "tin")
    private String tin;
 */
@Component
public class CryptoAttributeConverter implements AttributeConverter<String, String>  {
    private static String PASSWORD;
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";

    private static final int TAG_LENGTH_BIT = 128; // must be one of {128, 120, 112, 104, 96}
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static Environment environment;  // placeholder

    @Autowired
    public void setPassword(CryptoProperties cp) {
        CryptoAttributeConverter.PASSWORD=cp.getRdsColumnPassword();
    }

    @Override
    public String convertToDatabaseColumn(String pText) {
        byte[] salt = CryptoUtils.getRandomNonce(SALT_LENGTH_BYTE);
        byte[] iv = CryptoUtils.getRandomNonce(IV_LENGTH_BYTE);
        try {
            SecretKey aesKeyFromPassword = CryptoUtils.getAESKeyFromPassword(PASSWORD.toCharArray(), salt);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] cipherText = cipher.doFinal(pText.getBytes());
            byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length)
                    .put(iv)
                    .put(salt)
                    .put(cipherText)
                    .array();
            return Base64.getEncoder().encodeToString(cipherTextWithIvSalt);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 InvalidKeySpecException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String cText) {
        byte[] decode = Base64.getDecoder().decode(cText.getBytes(UTF_8));
        ByteBuffer bb = ByteBuffer.wrap(decode);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);
        byte[] salt = new byte[SALT_LENGTH_BYTE];
        bb.get(salt);
        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);
        try {
            SecretKey aesKeyFromPassword = CryptoUtils.getAESKeyFromPassword(PASSWORD.toCharArray(), salt);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, aesKeyFromPassword, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, UTF_8);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException |
                 InvalidKeySpecException | NoSuchAlgorithmException |InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
    }
}