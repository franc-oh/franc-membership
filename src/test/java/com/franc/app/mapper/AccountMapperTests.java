package com.franc.app.mapper;

import com.franc.app.vo.AccountVO;
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
    @DisplayName("전체조회")
    public void account_findAll_test() throws Exception {
        // # Given

        // # When
        List<AccountVO> accountVOList = accountMapper.findAll();

        // # Then
        assertThat(accountVOList).isNotEmpty();
        assertThat(accountVOList.get(0).getAccountGrade()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("PK_해당건_조회")
    public void findById_test() throws Exception {
        // # Given
        Long accountId = 3L;

        // # When
        AccountVO accountVo = accountMapper.findById(accountId);

        // # Then
        assertThat(accountVo).isNotNull();
        assertThat(accountVo.getAccountId()).isEqualTo(accountId);
        assertThat(accountVo.getAccountNm()).isEqualTo("사용자1");
        assertThat(accountVo.getStatus()).isEqualTo('9');
        assertThat(accountVo.getAccountGrade()).isEqualTo("USER");
    }

}