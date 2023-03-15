package com.franc.app.service;

import com.franc.app.code.Status;
import com.franc.app.exception.BizException;
import com.franc.app.exception.ExceptionResult;
import com.franc.app.mapper.MyMembershipMapper;
import com.franc.app.util.DateUtil;
import com.franc.app.util.NumberUtil;
import com.franc.app.vo.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyMembershipService {
    private static final Logger logger = LoggerFactory.getLogger(MyMembershipService.class);

    private final MyMembershipMapper myMembershipMapper;

    private final AccountService accountService;


    /**
     * 바코드 생성 (현재일자 + 시퀀스)
     * @return
     * @throws Exception
     */
    public synchronized String getBarcode() throws Exception {

        // # 1. 바코드 시퀀스 생성
        Integer seq = myMembershipMapper.getBarcodeSeq();
        if(seq == null) {
            throw new BizException(ExceptionResult.CREATE_BARCODE_FAIL);
        }

        String fullSeq = String.format("%06d", seq);

        // #2. 바코드 생성(현재일자 + 시퀀스(6자리 패딩)
        StringBuilder barcode = new StringBuilder(DateUtil.nowDateToString());
        barcode.append(fullSeq);

        logger.debug("createBarcode => " + barcode.toString());

        return barcode.toString();
    }


    /**
     * 멤버십 가입
     * @param paramVo
     * @throws Exception
     */
    public void save(MyMembershipVO paramVo) throws Exception {
        logger.info("My Membership Save Start : " + paramVo.toString());

        // #1. 해당 ID로 등록된 멤버십 가져오기
        Map<String, Object> myMembershipIdMap = new HashMap<>();
        myMembershipIdMap.put("accountId", paramVo.getAccountId());
        myMembershipIdMap.put("mspId", paramVo.getMspId());
        MyMembershipVO myMembershipVo = myMembershipMapper.findById(myMembershipIdMap);

        // #2. 멤버십이 이미 있는 경우 '상태'에 따라 분기처리 (탈퇴 | 중복)
        boolean withdrawal = false;
        if(myMembershipVo != null) {
            // '사용' 상태 => 중복 건
            if(Status.USE.getCode() == myMembershipVo.getStatus()) {
                throw new BizException(ExceptionResult.ALREADY_JOIN_MEMBERSHIP);
            }

            // '탈퇴' 상태 => 탈퇴 당일 재가입인지 체크 및 탈퇴 플래그 변경
            if(Status.WITHDRAWAL.getCode() == myMembershipVo.getStatus()) {
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

        logger.info("My Membership Save Success!!");

    }

    /**
     * 멤버십 탈퇴
     * @param paramVO
     * @throws Exception
     */
    public void withdrawal(MyMembershipVO paramVO) throws Exception {
        logger.info("My Membership Withdrawal Start : " + paramVO.toString());

        // #1. 해당 ID로 등록된 멤버십 가져오기
        Map<String, Object> myMembershipIdMap = new HashMap<>();
        myMembershipIdMap.put("accountId", paramVO.getAccountId());
        myMembershipIdMap.put("mspId", paramVO.getMspId());
        MyMembershipVO myMembershipVo = myMembershipMapper.findById(myMembershipIdMap);

        // #2. 멤버십 탈퇴가능여부 검증
        if(myMembershipVo == null) {
            throw new BizException(ExceptionResult.NOT_JOIN_MEMBERSHIP);
        }
        if(Status.WITHDRAWAL.getCode() == myMembershipVo.getStatus() || myMembershipVo.getWithdrawalDate() != null) {
            throw new BizException(ExceptionResult.ALREADY_WITHDRAWAL_MEMBERSHIP);
        }

        myMembershipMapper.withdrawal(paramVO);

        logger.info("My Membership Withdrawal Success!!");
    }

    /**
     * 멤버십 적립
     * @param paramMap {barCd, franchisseId, tradeAmt}
     * @return
     * @throws Exception
     */
    public MyMspDetailInfoVO accum(Map<String, Object> paramMap) throws Exception {
        logger.info("My Membership Accum Start : " + paramMap.toString());

        // #1. 바코드로 상세가입정보 가져오기
        String barCd = String.valueOf(paramMap.get("barCd"));
        String franchiseeId = String.valueOf(paramMap.get("franchiseeId"));
        int tradeAmt = Integer.parseInt(String.valueOf(paramMap.get("tradeAmt")));
        MyMspDetailInfoVO response = myMembershipMapper.findDetailByBarCdAndFranchiseeId(barCd, franchiseeId);
        if(response == null)
            throw new BizException(ExceptionResult.NOT_FOUND_BARCODE_INFO);

        MembershipVO membershipInfoVO = response.getMembershipInfo();
        MembershipFranchiseeVO franchiseeInfoVO = response.getFranchiseeInfo();
        MembershipGradeVO gradeBenefitInfoVO = response.getGradeBenefitInfo();

        if(membershipInfoVO == null || franchiseeInfoVO == null || gradeBenefitInfoVO == null)
            throw new BizException(ExceptionResult.NOT_FOUND_MEMBERSHIP);

        // #2. 가져온 상세정보를 통해 유효성 체크 및 적립데이터 셋팅
        if(membershipInfoVO.getStatus() != Status.USE.getCode())
            throw new BizException(ExceptionResult.NOT_ACTIVE_MEMBERSHIP);
        if(franchiseeInfoVO.getStatus() != Status.USE.getCode())
            throw new BizException(ExceptionResult.NOT_ACTIVE_FRANCHISEE);

        Long accountId = response.getAccountId();
        // 계정 체크
        AccountVO accountVO = accountService.getInfoAndCheckStatus(accountId);

        // #3. 적립내역등록
        String mspId = response.getMspId();
        int accumRat = response.getGradeBenefitInfo().getAccumRat();
        int accumPoint = NumberUtil.getCalcPerAmt(tradeAmt, accumRat);
        String expireYmd = DateUtil.getAddMonth(membershipInfoVO.getActiveMonths());

        MyMembershipAccumHisVO myMembershipAccumHisVO = MyMembershipAccumHisVO.builder()
                .cancelBarCd(getBarcode())
                .accountId(accountId)
                .mspId(mspId)
                .franchiseeId(franchiseeId)
                .tradeAmt(tradeAmt)
                .mspGradeCd(response.getMspGradeCd())
                .accumRat(accumRat)
                .accumPoint(accumPoint)
                .expireYmd(expireYmd)
                .build();

        myMembershipMapper.saveAccumHis(myMembershipAccumHisVO);
        response.setProcInfo(myMembershipAccumHisVO);

        logger.info("My Membership Accum Success!!");

        return response;
    }

    /**
     * 사용자의 멤버십 총 적립금 가져오기
     * @param accountId
     * @param mspId
     * @return
     * @throws Exception
     */
    public int getMyMembershipTotalAccumPoint(Long accountId, String mspId) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("accountId", accountId);
        paramMap.put("mspId", mspId);
        int totalAccumPoint = myMembershipMapper.getMyMembershipTotalAccumPoint(paramMap);

        logger.info("TotalAccumPoint : " + totalAccumPoint);

        return totalAccumPoint;
    }

    /**
     * 총액에 따른 등급가져오기
     * @param mspId
     * @param point
     * @return
     * @throws Exception
     */
    public MembershipGradeVO getMembershipGradeByPoint(String mspId, int point) throws Exception {
        Map<String, Object> getGradeParamMap = new HashMap<>();
        getGradeParamMap.put("point", point);
        getGradeParamMap.put("mspId", mspId);

        MembershipGradeVO gradeVO = myMembershipMapper.getMembershipGradeByPoint(getGradeParamMap);
        if(gradeVO == null)
          throw new BizException(ExceptionResult.NOT_FOUND_GRADE);

        logger.info("targetGrade : " + gradeVO.getMspGradeCd());

        return gradeVO;
    }

    /**
     * 등급 및 총액 업데이트
     * @param vo
     * @throws Exception
     */
    public void updatePointAndGrade(MyMembershipVO vo) throws Exception {
        myMembershipMapper.updatePointAndGrade(vo);
    }

    /**
     * 적립내역조회
     * @param vo
     * @return
     * @throws Exception
     */
    public List<MyMembershipAccumHisVO> findAllAccumHis(MyMembershipAccumHisVO vo) throws Exception {
        vo.setPaging();

        return myMembershipMapper.findAllAccumHis(vo);
    }

    /**
     * 적립상세조회
     * @param cancelBarCd
     * @return
     * @throws Exception
     */
    public MyMspAccumDetailInfoVO findByIdAccumHisDetail(String cancelBarCd) throws Exception {
        MyMspAccumDetailInfoVO vo = myMembershipMapper.findByIdAccumHisDetail(cancelBarCd);
        vo.setStatusNm(Status.of(vo.getStatus()).getName());
        return vo;
    }

}
