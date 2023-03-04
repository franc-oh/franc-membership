package com.franc.app.service;

import com.franc.app.code.Code;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.MyMembershipMapper;
import com.franc.app.util.DateUtil;
import com.franc.app.vo.MyMembershipVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyMembershipService {

    private final MyMembershipMapper myMembershipMapper;


    /**
     * 바코드 생성 (현재일자 + 시퀀스)
     * @return
     * @throws Exception
     */
    public String getBarcode() throws Exception {

        // # 1. 바코드 시퀀스 생성
        Integer seq = myMembershipMapper.getBarcodeSeq();
        if(seq == null) {
            throw new BizException(ExceptionResult.CREATE_BARCODE_FAIL);
        }

        String fullSeq = String.format("%06d", seq);

        // #2. 바코드 생성(현재일자 + 시퀀스(6자리 패딩)
        StringBuilder barcode = new StringBuilder(DateUtil.nowDateToString());
        barcode.append(fullSeq);

        log.debug("createBarcode => " + barcode.toString());

        return barcode.toString();
    }


    /**
     * 멤버십 가입
     * @param paramVo
     * @throws Exception
     */
    public void save(MyMembershipVO paramVo) throws Exception {
        log.info("My Membership Save Start : " + paramVo.toString());

        // #1. 해당 ID로 등록된 멤버십 가져오기
        Map<String, Object> myMembershipIdMap = new HashMap<>();
        myMembershipIdMap.put("accountId", paramVo.getAccountId());
        myMembershipIdMap.put("mspId", paramVo.getMspId());
        MyMembershipVO myMembershipVo = myMembershipMapper.findById(myMembershipIdMap);

        // #2. 멤버십이 이미 있는 경우 '상태'에 따라 분기처리 (탈퇴 | 중복)
        boolean withdrawal = false;
        if(myMembershipVo != null) {
            // '사용' 상태 => 중복 건
            if(Code.STATUS_USE == myMembershipVo.getStatus()) {
                throw new BizException(ExceptionResult.ALREADY_JOIN_MEMBERSHIP);
            }

            // '탈퇴' 상태 => 탈퇴 당일 재가입인지 체크 및 탈퇴 플래그 변경
            if(Code.STATUS_WITHDRAWAL == myMembershipVo.getStatus()) {
                if(DateUtil.compareNow(myMembershipVo.getWithdrawalDate()) >= 0) {
                    throw new BizException(ExceptionResult.RE_JOIN_NOT_POSSIBLE_WITHDRAWAL);
                }

                withdrawal = true;
            }
        }

        // #3. 탈퇴여부에 따라 등록/재가입처리
        if(!withdrawal) {
            // 바코드 생성
            String barCd = getBarcode();
            paramVo.setBarCd(barCd);
            myMembershipMapper.save(paramVo);
        } else {
            myMembershipMapper.rejoin(paramVo);
        }

        log.info("My Membership Save Success!!");

    }


}
