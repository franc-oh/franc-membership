package com.franc.app.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.app.code.Code;
import com.franc.app.dto.MembershipFindAllRequestDTO;
import com.franc.app.dto.MembershipFindByIdRequestDTO;
import com.franc.app.exception.ControllerExceptionHandler;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.service.AccountService;
import com.franc.app.service.MembershipService;
import com.franc.app.vo.AccountVO;
import com.franc.app.vo.MembershipVO;
import com.franc.app.vo.MspAndMyMspInfoVO;
import com.franc.app.vo.MyMembershipVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTests {

    @InjectMocks
    private MembershipController membershipController;

    @Mock
    private MembershipService membershipService;

    @Mock
    private AccountService accountService;

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private static final String FIND_ALL_URL = "/api/msp/membership/all";
    private static final String FIND_BY_ID_URL = "/api/msp/membership";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(membershipController)
                .setControllerAdvice(ControllerExceptionHandler.class)
                .build();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }


    @Test
    @DisplayName("멤버십_전체조회_실패_필수요청값_없음")
    public void findAll_fail_valid() throws Exception {
        // # 1. Given
        MembershipFindAllRequestDTO requestDTO = MembershipFindAllRequestDTO.builder().build();

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(FIND_ALL_URL)
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
    @DisplayName("멤버십_전체조회_성공")
    public void findAll_success() throws Exception {
        // # 1. Given
        String mspId = "M1111111";

        MembershipFindAllRequestDTO requestDTO = MembershipFindAllRequestDTO.builder()
                .accountId(5L)
                .joinYn("")
                .pageLimit(1)
                .pageNo(1)
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());


        List<MembershipVO> mockMspList = new ArrayList<>();
        mockMspList.add(MembershipVO.builder().mspId(mspId).build());
        when(membershipService.findAllOrMyMspList(any(MembershipVO.class)))
                .thenReturn(mockMspList);

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(FIND_ALL_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(Code.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(Code.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("membershipCnt").value(1))
                .andExpect(jsonPath("$.membershipList[0].mspId").value(mspId));


    }

    @Test
    @DisplayName("멤버십_상세조회_실패_필수요청값_없음")
    public void findById_fail_valid() throws Exception {
        // # 1. Given
        MembershipFindByIdRequestDTO requestDTO = MembershipFindByIdRequestDTO.builder().build();

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(FIND_BY_ID_URL)
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
    @DisplayName("멤버십_상세조회_실패_조회건_없음")
    public void findById_fail_not_found() throws Exception {
        // # 1. Given
        MembershipFindByIdRequestDTO requestDTO = MembershipFindByIdRequestDTO.builder()
                .accountId(5L)
                .mspId("123456")
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        when(membershipService.findByIdAndMyMspInfo(any(MspAndMyMspInfoVO.class)))
                .thenReturn(null);

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(FIND_BY_ID_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("resultCode").value(ExceptionResult.NOT_FOUND_MSP_INFO.getCode().value()))
                .andExpect(jsonPath("resultMessage").value(ExceptionResult.NOT_FOUND_MSP_INFO.getMessage()));
    }


    @Test
    @DisplayName("멤버십_상세조회_성공")
    public void findById_success() throws Exception {
        // # 1. Given
        Long accountId = 5L;
        String mspId = "1234556";

        MembershipFindByIdRequestDTO requestDTO = MembershipFindByIdRequestDTO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .build();

        when(accountService.getInfoAndCheckStatus(anyLong()))
                .thenReturn(AccountVO.builder().build());

        when(membershipService.findByIdAndMyMspInfo(any(MspAndMyMspInfoVO.class)))
                .thenReturn(MspAndMyMspInfoVO.builder()
                        .mspId(mspId)
                        .myMspInfo(MyMembershipVO.builder()
                                .accountId(accountId)
                                .mspId(mspId)
                                .build())
                        .build());

        // # 2. When
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(FIND_BY_ID_URL)
                        .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .accept(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                        .content(objectMapper.writeValueAsString(requestDTO))
        ).andDo(print());

        // # 3. Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("resultCode").value(Code.RESPONSE_CODE_SUCCESS))
                .andExpect(jsonPath("resultMessage").value(Code.RESPONSE_MESSAGE_SUCCESS))
                .andExpect(jsonPath("mspId").value(mspId))
                .andExpect(jsonPath("$.myMembershipInfo.accountId").value(accountId));
    }
}
