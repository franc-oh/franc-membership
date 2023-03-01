package com.franc.app.membership.mapper;

import com.franc.app.membership.vo.AccountVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountMapperTests {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    @DisplayName("회원_전체_조회_테스트")
    public void account_findAll_test() throws Exception {
        // # Given

        // # When
        List<AccountVo> accountVoList = accountMapper.findAll();

        // # Then
        assertThat(accountVoList).isNotEmpty();
        assertThat(accountVoList.get(0).getAccountGrade()).isEqualTo("ADMIN");
    }

}