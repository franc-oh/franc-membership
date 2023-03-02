package com.franc.app.config;

import com.franc.app.util.JasyptUtil;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
public class JasyptConfigTest {

    @Value("${jasypt.encryptor.password}")
    private String encryptKey;

    @Test
    @DisplayName("jasyptConfig_설정값_테스트")
    public void jasypt_config_test() {
        // # Given
        String value = "test";
        String encValue = jasyptEncoding(value);

        // # When
        String decValue = jasyptDecoding(encValue);

        // # Then
        assertThat(decValue).isEqualTo(value);

    }

    @Test
    @DisplayName("jasypt_유틸_테스트")
    public void jasypt_util_test() {
        // # Given
        String value = "test";
        String encValue = JasyptUtil.encoding(value);

        // # When
        String decValue = JasyptUtil.decoding(encValue);

        // # Then
        assertThat(decValue).isEqualTo(value);

    }






    public String jasyptEncoding(String value) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptKey);
        config.setAlgorithm("PBEWithMD5AndTripleDES"); // 권장되는 기본 알고리즘
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        return encryptor.encrypt(value);
    }

    public String jasyptDecoding(String value) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptKey);
        config.setAlgorithm("PBEWithMD5AndTripleDES"); // 권장되는 기본 알고리즘
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        return encryptor.decrypt(value);
    }

}