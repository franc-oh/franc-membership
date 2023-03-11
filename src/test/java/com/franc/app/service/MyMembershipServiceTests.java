package com.franc.app.service;

import com.franc.app.code.MembershipGrade;
import com.franc.app.code.Status;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.MyMembershipMapper;
import com.franc.app.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyMembershipServiceTests {

    @InjectMocks
    private MyMembershipService myMembershipService;

    @Mock
    private MyMembershipMapper myMembershipMapper;

    @Mock
    private AccountService accountService;

    @Spy
    private ModelMapper modelMapper;


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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.USE.getCode(),
                100, MembershipGrade.COMMON.getCode(), "20230304173630000001", null);

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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.WITHDRAWAL.getCode(),
                100, MembershipGrade.COMMON.getCode(), "20230304173630000001", LocalDateTime.now());

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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.USE.getCode(),
                0, MembershipGrade.COMMON.getCode(), "20230304173630000001", null);

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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.WITHDRAWAL.getCode(),
                0, MembershipGrade.COMMON.getCode(), "20230304173630000001", LocalDateTime.now().minusDays(1));

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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.WITHDRAWAL.getCode(),
                0, MembershipGrade.COMMON.getCode(), "20230304173630000001", LocalDateTime.now().minusDays(1));

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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.WITHDRAWAL.getCode(),
                0, MembershipGrade.COMMON.getCode(), "20230304173630000001", LocalDateTime.now().minusDays(1));

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
        MyMembershipVO vo = buildVo(accountId, mspId, Status.USE.getCode(),
                0, MembershipGrade.COMMON.getCode(), "20230304173630000001", null);

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

    @Test
    @DisplayName("멤버십적립실패_바코드_데이터_없음")
    public void accum_fail_no_data() throws Exception {
        // # 1. Given
        String barCd = "20230309173630000001";
        String franchiseeId = "F230228000002";
        int tradeAmt = 10000;

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("barCd", barCd);
        requestMap.put("franchiseeId", franchiseeId);
        requestMap.put("tradeAmt", tradeAmt);

        when(myMembershipMapper.findDetailByBarCdAndFranchiseeId(anyString(), anyString()))
                .thenReturn(null);

        // # 2. When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.accum(requestMap));

        // # 3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_FOUND_BARCODE_INFO);

        verify(myMembershipMapper, times(1)).findDetailByBarCdAndFranchiseeId(anyString(), anyString());
    }

    @Test
    @DisplayName("멤버십적립실패_멤버십관련정보_없음")
    public void accum_fail_not_found_membership() throws Exception {
        // # 1. Given
        int totalAccumPoint = 0;
        String barCd = "20230309173630000001";
        String franchiseeId = "F230228000002";
        int tradeAmt = 10000;

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("barCd", barCd);
        requestMap.put("franchiseeId", franchiseeId);
        requestMap.put("tradeAmt", tradeAmt);

        MyMspDetailInfoVO myMspDetailInfoVO = MyMspDetailInfoVO.builder()
                        .accountId(accountId)
                        .mspId(mspId)
                        .barCd(barCd)
                        .build();

        when(myMembershipMapper.findDetailByBarCdAndFranchiseeId(anyString(), anyString()))
                .thenReturn(myMspDetailInfoVO);

        // # 2. When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.accum(requestMap));

        // # 3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_FOUND_MEMBERSHIP);

        verify(myMembershipMapper, times(1)).findDetailByBarCdAndFranchiseeId(anyString(), anyString());
    }

    @Test
    @DisplayName("멤버십적립실패_멤버십상태_정상아님")
    public void accum_fail_not_use_membership() throws Exception {
        // # 1. Given
        int totalAccumPoint = 0;
        String barCd = "20230309173630000001";
        String franchiseeId = "F230228000002";
        int tradeAmt = 10000;

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("barCd", barCd);
        requestMap.put("franchiseeId", franchiseeId);
        requestMap.put("tradeAmt", tradeAmt);

        MyMspDetailInfoVO myMspDetailInfoVO = MyMspDetailInfoVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .membershipInfo(MembershipVO.builder()
                        .mspId(mspId)
                        .status(Status.STOP.getCode())
                        .build())
                .franchiseeInfo(MembershipFranchiseeVO.builder().build())
                .gradeBenefitInfo(MembershipGradeVO.builder().build())
                .build();

        when(myMembershipMapper.findDetailByBarCdAndFranchiseeId(anyString(), anyString()))
                .thenReturn(myMspDetailInfoVO);

        // # 2. When
        BizException exception =
                assertThrows(BizException.class, () -> myMembershipService.accum(requestMap));

        // # 3. Then
        assertThat(exception.getClass()).isEqualTo(BizException.class);
        assertThat(exception.getResult()).isEqualTo(ExceptionResult.NOT_ACTIVE_MEMBERSHIP);

        verify(myMembershipMapper, times(1)).findDetailByBarCdAndFranchiseeId(anyString(), anyString());
    }

    @Test
    @DisplayName("멤버십적립성공")
    public void accum_success() throws Exception {
        // # 1. Given
        int totalAccumPoint = 0;
        String barCd = "20230309173630000001";
        String franchiseeId = "F230228000002";
        int tradeAmt = 10000;

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("barCd", barCd);
        requestMap.put("franchiseeId", franchiseeId);
        requestMap.put("tradeAmt", tradeAmt);

        MyMspDetailInfoVO myMspDetailInfoVO = MyMspDetailInfoVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .mspGradeCd(MembershipGrade.COMMON.getCode())
                .membershipInfo(MembershipVO.builder()
                        .mspId(mspId)
                        .status(Status.USE.getCode())
                        .build())
                .franchiseeInfo(MembershipFranchiseeVO.builder()
                        .mspId(mspId)
                        .franchiseeId(franchiseeId)
                        .status(Status.USE.getCode())
                        .build())
                .gradeBenefitInfo(MembershipGradeVO.builder()
                        .mspId(mspId)
                        .mspGradeCd(MembershipGrade.COMMON.getCode())
                        .accumRat(3)
                        .build())
                .build();

        when(myMembershipMapper.findDetailByBarCdAndFranchiseeId(anyString(), anyString()))
                .thenReturn(myMspDetailInfoVO);

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        doNothing().when(myMembershipMapper)
                .saveAccumHis(any(MyMembershipAccumHisVO.class));

        // # 2. When
        MyMspDetailInfoVO resultVO = myMembershipService.accum(requestMap);

        // # 3. Then

        verify(myMembershipMapper, times(1)).findDetailByBarCdAndFranchiseeId(anyString(), anyString());
        verify(myMembershipMapper, times(1)).saveAccumHis(any(MyMembershipAccumHisVO.class));
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
