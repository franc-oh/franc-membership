package com.franc.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.app.code.Code;
import com.franc.app.dto.MyMembershipJoinRequestDTO;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.exception.ControllerExceptionHandler;
import com.franc.app.service.AccountService;
import com.franc.app.service.MyMembershipService;
import com.franc.app.vo.AccountVO;
import com.franc.app.vo.MyMembershipVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
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

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(myMembershipController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper = new ObjectMapper();
    }

    private static final String JOIN_URL = "/api/msp/my_membership";

    @ParameterizedTest
    @MethodSource("join_fail_valid_params")
    @DisplayName("멤버십가입_실패_필수값_안들어옴")
    public void join_fail_valid(MyMembershipJoinRequestDTO requestDTO) throws Exception {
        // # 1. Given

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(JOIN_URL)
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
                MockMvcRequestBuilders.post(JOIN_URL)
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
                MockMvcRequestBuilders.post(JOIN_URL)
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
                MockMvcRequestBuilders.post(JOIN_URL)
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


}

