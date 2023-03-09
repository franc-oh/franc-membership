package com.franc.app.config;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.Stream;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
public class H2FunctionConfigTests {

    @ParameterizedTest
    @MethodSource("strToDateParams")
    @DisplayName("STR_TO_DATE")
    public void str_to_date(String date, String format) throws Exception {
        // # 1. Given

        // # 2. When
        System.out.println(H2FunctionConfig.strToDate(date, format));
    }

    public static Stream<Arguments> strToDateParams() {
        return Stream.of(
                Arguments.of("20230309", "%Y%m%d"),
                Arguments.of("20230309152030", "%Y%m%d%H%i%s")
        );
    }



}
