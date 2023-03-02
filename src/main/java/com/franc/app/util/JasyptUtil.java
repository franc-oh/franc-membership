package com.franc.app.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Jasypt 암/복호화 유틸
 */
@Component
public class JasyptUtil {
    private static String encryptKey;

    @Value("${jasypt.encryptor.password}")
    public void setKey(String value) {
        encryptKey = value;
    }

    public static StandardPBEStringEncryptor getEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptKey);
        config.setAlgorithm("PBEWithMD5AndTripleDES"); // 권장되는 기본 알고리즘
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        return encryptor;
    }

    /**
     * 암호화
     * @param value
     * @return
     */
    public static String encoding(String value) {
        return getEncryptor().encrypt(value);
    }

    /**
     * 복호화
     * @param encValue
     * @return
     */
    public static String decoding(String encValue) {
        return getEncryptor().decrypt(encValue);
    }

}
