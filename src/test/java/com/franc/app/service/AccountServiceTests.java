package com.franc.app.service;


import com.franc.app.code.AccountGrade;
import com.franc.app.code.Status;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.AccountMapper;
import com.franc.app.vo.AccountVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountMapper accountMapper;

    @Test
    @DisplayName("PK_사용자_가져오기_실패_사용자없음")
    public void getInfoAndCheckStatus_fail_not_found_account() throws Exception {
        // # 1. Given
        Long accountId = 6L;

        when(accountMapper.findById(anyLong()))
                .thenReturn(null);

        // # 2. When
        BizException exception =
                assertThrows(BizException.class, () -> accountService.getInfoAndCheckStatus(accountId));

        // # 3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_FOUND_ACCOUNT);

    }

    @ParameterizedTest
    @MethodSource("notActiveAccountList")
    @DisplayName("PK_사용자_가져오기_실패_유효하지않은상태")
    public void getInfoAndCheckStatus_fail_not_available_status(AccountVO paramVo) throws Exception {
        // # 1. Given
        Long accountId = paramVo.getAccountId();


        when(accountMapper.findById(anyLong()))
                .thenReturn(paramVo);

        // # 2. When
        BizException exception =
                assertThrows(BizException.class, () -> accountService.getInfoAndCheckStatus(accountId));

        // # 3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_ACTIVE_ACCOUNT);
    }

    @Test
    @DisplayName("PK_사용자_가져오기_성공")
    public void getInfoAndCheckStatus_success() throws Exception {
        // # 1. Given
        Long accountId = 1L;
        AccountVO paramVo = AccountVO.builder()
                .accountId(accountId)
                .accountNm("테스트")
                .status(Status.USE.getCode())
                .accountGrade(AccountGrade.USER.getCode())
                .build();

        when(accountMapper.findById(anyLong()))
                .thenReturn(paramVo);

        // # 2. When
        AccountVO accountVo = accountService.getInfoAndCheckStatus(accountId);

        // # 3. Then
        assertThat(accountVo).isNotNull();
        assertThat(accountVo.getAccountId()).isEqualTo(accountId);
        assertThat(accountVo.getStatus()).isEqualTo(Status.USE.getCode());

        verify(accountMapper, times(1)).findById(accountId);
    }

    public static Stream<Arguments> notActiveAccountList() {
        return Stream.of(
                Arguments.of(AccountVO.builder().accountId(3L).status(Status.STOP.getCode()).build()),
                Arguments.of(AccountVO.builder().accountId(3L).status(Status.WITHDRAWAL.getCode()).build())
        );
    }

}
