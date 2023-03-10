package com.franc.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.franc.app.code.Code;
import com.franc.app.dto.*;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.service.AccountService;
import com.franc.app.service.MyMembershipService;
import com.franc.app.util.NumberUtil;
import com.franc.app.vo.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/msp/my_membership")
@RequiredArgsConstructor
public class MyMembershipController {
    private static final Logger logger = LoggerFactory.getLogger(MyMembershipController.class);


    private final MyMembershipService myMembershipService;

    private final AccountService accountService;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

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

    /**
     * 멤버십 탈퇴
     * @param request
     * @return
     * @throws Exception
     */
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

    @PostMapping("/accum")
    public ResponseEntity<?> accum(@RequestBody @Valid MyMembershipAccumRequestDTO request) throws Exception {
        MyMembershipAccumResponseDTO response = new MyMembershipAccumResponseDTO();

        logger.info("멤버십적립_Request => {}", request.toString());

        // #1. 바코드로 적립처리 + 멤버십상세정보 가져오기
        Map<String, Object> requestMap = objectMapper.convertValue(request, HashMap.class);

        MyMspDetailInfoVO accumVO = myMembershipService.accum(requestMap);
        if (accumVO == null) {
            throw new BizException(ExceptionResult.NOT_FOUND_MEMBERSHIP);
        }

        Long accountId = accumVO.getAccountId();
        String mspId = accumVO.getMspId();
        int tradeAmt = request.getTradeAmt();

        // #2. 적립총액 가져오기 및 총액에 해당하는 등급 가져오기
        int totalAccumPoint = myMembershipService.getMyMembershipTotalAccumPoint(accountId, mspId);
        MembershipGradeVO targetGradeVO = myMembershipService.getMembershipGradeByPoint(mspId, totalAccumPoint);
        String targetGradeCd = targetGradeVO.getMspGradeCd();

        // #3. 적립총액 및 등급 갱신
        myMembershipService.updatePointAndGrade(MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .mspGradeCd(targetGradeCd)
                .totalAccumPoint(totalAccumPoint)
                .build());

        MembershipVO membershipInfo = accumVO.getMembershipInfo();
        MembershipGradeVO gradeBenefitInfo = accumVO.getGradeBenefitInfo();
        MembershipFranchiseeVO franchiseeInfo = accumVO.getFranchiseeInfo();
        MyMembershipAccumHisVO procInfo = accumVO.getProcInfo();
        int discRat = gradeBenefitInfo.getDiscRat();

        // #4. 응답
        response.setResultCode(Code.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(Code.RESPONSE_MESSAGE_SUCCESS);
        response.setAccountId(accountId);
        response.setMspId(mspId);
        response.setTradeAmt(tradeAmt);
        response.setMspNm(membershipInfo.getMspNm());
        response.setFranchiseeId(franchiseeInfo.getFranchiseeId());
        response.setFranchiseeNm(franchiseeInfo.getFranchiseeNm());
        response.setMspGradeCd(gradeBenefitInfo.getMspGradeCd());
        response.setAccumRat(procInfo.getAccumRat());
        response.setAccumPoint(procInfo.getAccumPoint());
        response.setDiscRat(discRat);
        response.setDiscAmt(NumberUtil.getCalcPerAmt(tradeAmt, discRat));
        response.setTotalAccumPoint(totalAccumPoint);
        response.setNextMspGradeCd(targetGradeCd);

        logger.info("멤버십적립_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
