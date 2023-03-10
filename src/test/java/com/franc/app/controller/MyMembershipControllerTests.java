package com.franc.app.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.app.code.Code;
import com.franc.app.dto.MyMembershipAccumRequestDTO;
import com.franc.app.dto.MyMembershipJoinRequestDTO;
import com.franc.app.dto.MyMembershipWithdrawalRequestDTO;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ControllerExceptionHandler;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.service.AccountService;
import com.franc.app.service.MyMembershipService;
import com.franc.app.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MyMembershipControllerTests {

    @InjectMocks
    private MyMembershipController myMembershipController;

    @Mock
    private MyMembershipService myMembershipService;

    @Mock
    private AccountService accountService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(myMembershipController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final String URL = "/api/msp/my_membership";
    private static final String ACCUM_URL = "/api/msp/my_membership/accum";

    @ParameterizedTest
    @MethodSource("join_fail_valid_params")
    @DisplayName("멤버십가입_실패_필수값_안들어옴")
    public void join_fail_valid(MyMembershipJoinRequestDTO requestDTO) throws Exception {
        // # 1. Given

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());


        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.PARAMETER_NOT_VALID.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.PARAMETER_NOT_VALID.getMessage()));
    }

    public static Stream<Arguments> join_fail_valid_params() {
        return Stream.of(
                Arguments.of(MyMembershipJoinRequestDTO.builder().build()),
                Arguments.of(MyMembershipJoinRequestDTO.builder().accountId(1L).build()),
                Arguments.of(MyMembershipJoinRequestDTO.builder().mspId("123").build())
            );
    }


    @Test
    @DisplayName("멤버십가입_실패_회원정보_에러")
    public void join_fail_account_err() throws Exception {
        // # 1. Given
        Long accountId = 6L;
        MyMembershipJoinRequestDTO requestDTO = MyMembershipJoinRequestDTO.builder()
                .accountId(accountId)
                .mspId("12345")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_ACCOUNT));

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_ACCOUNT.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_ACCOUNT.getMessage()));
    }

    @Test
    @DisplayName("멤버십가입_실패_기가입")
    public void join_fail_already_join() throws Exception {
        // # 1. Given
        Long accountId = 6L;
        MyMembershipJoinRequestDTO requestDTO = MyMembershipJoinRequestDTO.builder()
                .accountId(accountId)
                .mspId("12345")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        doThrow(new BizException(ExceptionResult.ALREADY_JOIN_MEMBERSHIP))
                .when(myMembershipService).save(any(MyMembershipVO.class));

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.ALREADY_JOIN_MEMBERSHIP.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.ALREADY_JOIN_MEMBERSHIP.getMessage()));
    }

    @Test
    @DisplayName("멤버십가입_성공")
    public void join_success() throws Exception {
        // # 1. Given
        Long accountId = 5L;
        MyMembershipJoinRequestDTO requestDTO = MyMembershipJoinRequestDTO.builder()
                .accountId(accountId)
                .mspId("12345")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        doNothing().when(myMembershipService).save(any(MyMembershipVO.class));


        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("resultCode").value(Code.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(Code.RESPONSE_MESSAGE_SUCCESS));

        verify(accountService, times(1)).getInfoAndCheckStatus(anyLong());
        verify(myMembershipService, times(1)).save(any(MyMembershipVO.class));
    }

    @Test
    @DisplayName("멤버십탈퇴_실패_필수값_안들어옴")
    public void withdrawal_fail_valid() throws Exception {
        // # 1. Given
        MyMembershipWithdrawalRequestDTO requestDTO = MyMembershipWithdrawalRequestDTO.builder().build();

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());


        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.PARAMETER_NOT_VALID.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.PARAMETER_NOT_VALID.getMessage()));
    }

    @Test
    @DisplayName("멤버십탈퇴_실패_회원정보_에러")
    public void withdrawal_fail_account_err() throws Exception {
        // # 1. Given
        Long accountId = 6L;
        MyMembershipWithdrawalRequestDTO requestDTO = MyMembershipWithdrawalRequestDTO.builder()
                .accountId(accountId)
                .mspId("12345")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_ACCOUNT));

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_ACCOUNT.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_ACCOUNT.getMessage()));
    }

    @Test
    @DisplayName("멤버십탈퇴_실패_이미탈퇴")
    public void withdrawal_fail_already() throws Exception {
        // # 1. Given
        Long accountId = 6L;
        MyMembershipWithdrawalRequestDTO requestDTO = MyMembershipWithdrawalRequestDTO.builder()
                .accountId(accountId)
                .mspId("12345")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        doThrow(new BizException(ExceptionResult.ALREADY_WITHDRAWAL_MEMBERSHIP))
                .when(myMembershipService).withdrawal(any(MyMembershipVO.class));

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.ALREADY_WITHDRAWAL_MEMBERSHIP.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.ALREADY_WITHDRAWAL_MEMBERSHIP.getMessage()));
    }

    @Test
    @DisplayName("멤버십탈퇴_성공")
    public void withdrawal_success() throws Exception {
        // # 1. Given
        Long accountId = 5L;
        MyMembershipJoinRequestDTO requestDTO = MyMembershipJoinRequestDTO.builder()
                .accountId(accountId)
                .mspId("12345")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        doNothing().when(myMembershipService).withdrawal(any(MyMembershipVO.class));


        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(Code.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(Code.RESPONSE_MESSAGE_SUCCESS));

        verify(accountService, times(1)).getInfoAndCheckStatus(anyLong());
        verify(myMembershipService, times(1)).withdrawal(any(MyMembershipVO.class));
    }


    /*
    1. 필수요청값체크
    2. 멤버십가입여부조회
    3. 검증 및 데이터 가져오기
        - 바코드에 해당하는 멤버십 있는지 확인
            - 멤버십 상태확인
        - 가맹점조회 및 체크
            - 상태확인
        - 현재 등급에 따른 멤버십 정책가져오기
    4. 적립저장

    5. 적립금처리 및 등급처리
        - 적립총액 계산
        - 총액에 따른 등급계산
    6. 맴버십 적립정보 응답
     */

    @Test
    @DisplayName("멤버십적립_바코드_실패_필수값")
    public void accum_fail_valid() throws Exception {
        // # 1. Given
        MyMembershipAccumRequestDTO requestDTO = MyMembershipAccumRequestDTO.builder().build();

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(ACCUM_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());


        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.PARAMETER_NOT_VALID.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.PARAMETER_NOT_VALID.getMessage()));
    }


    @Test
    @DisplayName("멤버십적립_바코드_실패_적립메소드_오류")
    public void accum_fail_proc_error() throws Exception {
        // # 1. Given
        MyMembershipAccumRequestDTO requestDTO = MyMembershipAccumRequestDTO.builder()
                .barCd("123")
                .franchisseId("2222")
                .tradeAmt(1000)
                .build();

        when(myMembershipService.accum(any(HashMap.class)))
                .thenThrow(new BizException(ExceptionResult.NOT_FOUND_BARCODE_INFO));

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(ACCUM_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());


        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_BARCODE_INFO.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_BARCODE_INFO.getMessage()));
    }

    @Test
    @DisplayName("멤버십적립_바코드_실패_적립메소드_리턴값없음")
    public void accum_fail_proc_not_response() throws Exception {
        // # 1. Given
        MyMembershipAccumRequestDTO requestDTO = MyMembershipAccumRequestDTO.builder()
                .barCd("123")
                .franchisseId("2222")
                .tradeAmt(1000)
                .build();

        when(myMembershipService.accum(any(HashMap.class)))
                .thenReturn(null);

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(ACCUM_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());


        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_MEMBERSHIP.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_MEMBERSHIP.getMessage()));
    }



    @Test
    @DisplayName("멤버십적립_성공")
    public void accum_success() throws Exception {
        // # 1. Given
        MyMembershipAccumRequestDTO requestDTO = MyMembershipAccumRequestDTO.builder()
                .barCd("123")
                .franchisseId("2222")
                .tradeAmt(1000)
                .build();


        String mspId = "123";
        when(myMembershipService.accum(any(HashMap.class)))
                .thenReturn(MyMspDetailInfoVO.builder()
                        .accountId(5L)
                        .mspId(mspId)
                        .franchiseeInfo(MembershipFranchiseeVO.builder()
                                .mspId(mspId)
                                .franchiseeId("2222")
                                .build())
                        .gradeBenefitInfo(MembershipGradeVO.builder()
                                .mspGradeCd("COMMON")
                                .mspId(mspId)
                                .build())
                        .membershipInfo(MembershipVO.builder()
                                .mspId(mspId).build())
                        .build());

        doNothing().when(myMembershipService).calcTotalPointAndUpdateGrade(anyLong(), anyString());
        when(myMembershipService.calcTotalPointAndUpdateGrade(anyLong(), anyString()))
                .thenReturn(MyMembershipVO.builder()
                        .totalAccumPoint(3000)
                        .mspGradeCd("COMMON")
                        .build());

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(ACCUM_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());


        // # 3. Then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("resultCode").value(Code.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(Code.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("mspId").value(mspId))
                .andExpect(jsonPath("$.franchiseeInfo.franchiseeId").value("2222"))
                .andExpect(jsonPath("$.membershipInfo.mspId").value(mspId))
                .andExpect(jsonPath("$.gradeBenefitInfo.mspGradeCd").value("COMMON"));

        verify(myMembershipService, times(1)).accum(any(HashMap.class));
        verify(myMembershipService, times(1)).calcTotalPointAndUpdateGrade(anyLong(), anyString());

    }


}

