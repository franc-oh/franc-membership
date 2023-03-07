package com.franc.app.controller;

import com.franc.app.code.Code;
import com.franc.app.dto.MyMembershipJoinRequestDTO;
import com.franc.app.dto.MyMembershipJoinResponseDTO;
import com.franc.app.dto.MyMembershipWithdrawalRequestDTO;
import com.franc.app.dto.MyMembershipWithdrawalResponseDTO;
import com.franc.app.service.AccountService;
import com.franc.app.service.MyMembershipService;
import com.franc.app.vo.AccountVO;
import com.franc.app.vo.MyMembershipVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/msp/my_membership")
@RequiredArgsConstructor
public class MyMembershipController {
    private static final Logger logger = LoggerFactory.getLogger(MyMembershipController.class);


    private final MyMembershipService myMembershipService;

    private final AccountService accountService;

    private final ModelMapper modelMapper;

    /**
     * 멤버십 가입
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping
    public ResponseEntity<?> join(@RequestBody @Valid MyMembershipJoinRequestDTO request) throws Exception {
        MyMembershipJoinResponseDTO response = new MyMembershipJoinResponseDTO();

        logger.info("멤버십가입_Request => {}", request.toString());

        // #1. 사용자 체크 및 가져오기
        AccountVO accountVO = accountService.getInfoAndCheckStatus(request.getAccountId());

        // #2. 가입
        MyMembershipVO requestVO = new MyMembershipVO();
        modelMapper.map(request, requestVO);
        myMembershipService.save(requestVO);

        // #3. 응답
        response.setResultCode(Code.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(Code.RESPONSE_MESSAGE_SUCCESS);

        logger.info("멤버십가입_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> withdrawal(@RequestBody @Valid MyMembershipWithdrawalRequestDTO request) throws Exception {
        MyMembershipWithdrawalResponseDTO response = new MyMembershipWithdrawalResponseDTO();

        logger.info("멤버십탈퇴_Request => {}", request.toString());

        // #1. 사용자 체크 및 가져오기
        AccountVO accountVO = accountService.getInfoAndCheckStatus(request.getAccountId());

        // #2. 탈퇴
        MyMembershipVO requestVO = new MyMembershipVO();
        modelMapper.map(request, requestVO);
        myMembershipService.withdrawal(requestVO);

        // #3. 응답
        response.setResultCode(Code.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(Code.RESPONSE_MESSAGE_SUCCESS);

        logger.info("멤버십탈퇴_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
