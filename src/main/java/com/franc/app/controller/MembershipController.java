package com.franc.app.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.franc.app.code.Code;
import com.franc.app.dto.MembershipFindAllRequestDTO;
import com.franc.app.dto.MembershipFindAllResponseDTO;
import com.franc.app.service.AccountService;
import com.franc.app.service.MembershipService;
import com.franc.app.vo.AccountVO;
import com.franc.app.vo.MembershipVO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/msp/membership")
@RequiredArgsConstructor
public class MembershipController {
    private static final Logger logger = LoggerFactory.getLogger(MembershipController.class);


    private final MembershipService membershipService;

    private final AccountService accountService;

    private final ObjectMapper objectMapper;

    private final ModelMapper modelMapper;

    @GetMapping("/all")
    public ResponseEntity<?> findAll(@RequestBody @Valid MembershipFindAllRequestDTO request) throws Exception {
        MembershipFindAllResponseDTO response = new MembershipFindAllResponseDTO();

        logger.info("멤버십_전체조회_Request => {}", request.toString());

        // #1. 사용자 체크 및 가져오기
        AccountVO accountVO = accountService.getInfoAndCheckStatus(request.getAccountId());

        // #2. 조회
        MembershipVO listParam = new MembershipVO();
        modelMapper.map(request, listParam);
        List<MembershipVO> membershipList = membershipService.findAllOrMyMspList(listParam);

        // #3. 응답
        response.setResultCode(Code.RESPONSE_CODE_SUCCESS);
        response.setResultMessage(Code.RESPONSE_MESSAGE_SUCCESS);

        if(!membershipList.isEmpty()) {
            response.setMembershipCnt(membershipList.size());
            response.setMembershipList(objectMapper.convertValue(membershipList,
                    TypeFactory.defaultInstance().constructCollectionType(List.class, MembershipFindAllResponseDTO.MembershipInfo.class)
            ));
        }

        logger.info("멤버십_전체조회_Response => {}", response.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
