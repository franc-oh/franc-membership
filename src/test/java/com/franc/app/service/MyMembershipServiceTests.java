package com.franc.app.service;

import com.franc.app.code.Code;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.MyMembershipMapper;
import com.franc.app.vo.MyMembershipVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyMembershipServiceTests {

    @InjectMocks
    private MyMembershipService myMembershipService;

    @Mock
    private MyMembershipMapper myMembershipMapper;


    Long accountId = 5L;
    String mspId = "M230227000001";



    @Test
    @DisplayName("바코드_생성_실패_바코드_쿼리_오류발생")
    public void create_barcode_test_fail1() throws Exception {
        // # Given

        when(myMembershipMapper.getBarcodeSeq()).thenReturn(null);

        // # When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.getBarcode());

        // # Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.CREATE_BARCODE_FAIL);
    }

    @Test
    @DisplayName("바코드_생성_성공")
    public void create_barcode_success() throws Exception {
        // # Given

        when(myMembershipMapper.getBarcodeSeq()).thenReturn(1);

        // # When
        String barcode = myMembershipService.getBarcode();

        // # Then
        assertThat(barcode.length()).isEqualTo(20);
        assertThat(barcode.substring(barcode.length()-1)).isEqualTo("1");
    }


    @Test
    @DisplayName("등록_실패_기가입")
    public void save_fail_already_join() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_USE,
                100, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", null);

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(vo);

        // # When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.save(vo));

        // # Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.ALREADY_JOIN_MEMBERSHIP);
    }

    @Test
    @DisplayName("등록_실패_탈퇴후1일안됨")
    public void save_fail_withdrawal_yet() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_WITHDRAWAL,
                100, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", LocalDateTime.now());

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(vo);

        // # When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.save(vo));

        // # Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.RE_JOIN_NOT_POSSIBLE_WITHDRAWAL);
    }

    @Test
    @DisplayName("등록_성공_신규")
    public void save_success_new_join() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_USE,
                0, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", null);

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(null);

        // # When
        myMembershipService.save(vo);

        // # Then
        verify(myMembershipMapper, times(1)).findById(any(HashMap.class));
        verify(myMembershipMapper, times(1)).save(any(MyMembershipVO.class));

    }

    @Test
    @DisplayName("등록_성공_재가입")
    public void save_success_re_join() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_WITHDRAWAL,
                0, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", LocalDateTime.now().minusDays(1));

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(vo);

        // # When
        myMembershipService.save(vo);

        // # Then
        verify(myMembershipMapper, times(1)).findById(any(HashMap.class));
        verify(myMembershipMapper, times(1)).rejoin(any(MyMembershipVO.class));

    }


    @Test
    @DisplayName("탈퇴_실패_미가입")
    public void withdrawal_fail_not_join() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_WITHDRAWAL,
                0, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", LocalDateTime.now().minusDays(1));

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(null);

        // # When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.withdrawal(vo));

        // # Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_JOIN_MEMBERSHIP);
    }

    @Test
    @DisplayName("탈퇴_실패_이미탈퇴")
    public void withdrawal_fail_already() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_WITHDRAWAL,
                0, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", LocalDateTime.now().minusDays(1));

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(vo);

        // # When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.withdrawal(vo));

        // # Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.ALREADY_WITHDRAWAL_MEMBERSHIP);
    }

    @Test
    @DisplayName("탈퇴_성공")
    public void withdrawal_success() throws Exception {
        // # Given
        MyMembershipVO vo = buildVo(accountId, mspId, Code.STATUS_USE,
                0, Code.MEMBERSHIP_GRADE_COMMON, "20230304173630000001", null);

        when(myMembershipMapper.findById(any(HashMap.class)))
                .thenReturn(vo);

        doNothing().when(myMembershipMapper)
                .withdrawal(any(MyMembershipVO.class));

        // # When
        myMembershipService.withdrawal(vo);

        // # Then
        verify(myMembershipMapper, times(1)).findById(any(HashMap.class));
        verify(myMembershipMapper, times(1)).withdrawal(any(MyMembershipVO.class));
    }


    public MyMembershipVO buildVo(Long accountId, String mspId, Character status,
                                  Integer totalAccumPoint, String mspGradeCd, String barCd, LocalDateTime withdrawalDate) {
        return MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .status(status)
                .totalAccumPoint(totalAccumPoint)
                .mspGradeCd(mspGradeCd)
                .barCd(barCd)
                .withdrawalDate(withdrawalDate)
                .build();
    }

}
