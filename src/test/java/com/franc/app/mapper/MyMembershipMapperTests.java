package com.franc.app.mapper;

import com.franc.app.code.Code;
import com.franc.app.util.DateUtil;
import com.franc.app.util.NumberUtil;
import com.franc.app.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MyMembershipMapperTests {

    @Autowired
    private MyMembershipMapper myMembershipMapper;

    Long accountId = 5L;
    String mspId = "M230227000001";

    @Test
    @DisplayName("바코드_생성용_시퀀스")
    public void create_barcode_seq() throws Exception {
        // # Given

        // # When
        Integer seq = myMembershipMapper.getBarcodeSeq();

        // # Then
        assertThat(seq).isNotNull();
        assertThat(seq).isGreaterThan(0);

    }

    @Test
    @DisplayName("멤버십_등록")
    @Transactional
    public void save() throws Exception {
        // # Given
        String barcode = createBarcode();
        String mspGradeCd = "COMMON";
        MyMembershipVO myMembershipVo = new MyMembershipVO().builder()
                .accountId(accountId)
                .mspId(mspId)
                .mspGradeCd(mspGradeCd)
                .barCd(barcode)
                .build();

        // # When
        myMembershipMapper.save(myMembershipVo);

        // findById
        Map<String, Object> findByIdParamMap = new HashMap<>();
        findByIdParamMap.put("accountId", accountId);
        findByIdParamMap.put("mspId", mspId);
        MyMembershipVO myMembership = myMembershipMapper.findById(findByIdParamMap);

        // # Then
        assertThat(myMembership).isNotNull();
        assertThat(myMembership.getAccountId()).isEqualTo(accountId);
        assertThat(myMembership.getMspId()).isEqualTo(mspId);
        assertThat(myMembership.getMspGradeCd()).isEqualTo(mspGradeCd);
        assertThat(myMembership.getBarCd()).isEqualTo(barcode);
        assertThat(myMembership.getStatus()).isEqualTo('1');
    }

    @Test
    @DisplayName("멤버십탈퇴")
    @Transactional
    public void withdrawal() throws Exception {
        // # 1. Given
        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(createBarcode())
                .build();

        myMembershipMapper.save(myMembershipVO);

        // # 2. When
        myMembershipMapper.withdrawal(myMembershipVO);

        // findById
        Map<String, Object> findByIdParamMap = new HashMap<>();
        findByIdParamMap.put("accountId", accountId);
        findByIdParamMap.put("mspId", mspId);
        MyMembershipVO myMembership = myMembershipMapper.findById(findByIdParamMap);

        // # 3. then
        assertThat(myMembership).isNotNull();
        assertThat(myMembership.getAccountId()).isEqualTo(accountId);
        assertThat(myMembership.getMspId()).isEqualTo(mspId);
        assertThat(myMembership.getStatus()).isEqualTo(Code.STATUS_WITHDRAWAL);
        assertThat(myMembership.getWithdrawalDate()).isNotNull();

    }


    /**
     * ! 내 멤버십 + 가맹점정보 + 현재 등급에 따른 멤버십 정책
     * ! 혜택저장 (INSERT)
     * 적립총액 계산
     * 총액에 따른 등급계산
     * 멤버십 등급 산출 (UPDATE)
     */
    @Test
    @DisplayName("바코드로_멤버십상세조회")
    @Transactional
    public void findDetailByBarCdAndFranchiseeId() throws Exception {
        // # 1. Given
        String barCd = createBarcode();
        String franchiseeId = "F230228000002";

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();

        myMembershipMapper.save(myMembershipVO);

        // # 2. When
        MyMspDetailInfoVO resultVO = myMembershipMapper.findDetailByBarCdAndFranchiseeId(barCd, franchiseeId);

        // # 3. Then
        assertThat(resultVO).isNotNull();
        assertThat(resultVO.getAccountId()).isEqualTo(accountId);
        assertThat(resultVO.getMspId()).isEqualTo(mspId);
        assertThat(resultVO.getStatus()).isEqualTo(Code.STATUS_USE);
        assertThat(resultVO.getFranchiseeInfo().getFranchiseeId()).isNotNull();
        assertThat(resultVO.getGradeBenefitInfo().getMspGradeCd()).isNotNull();
        assertThat(resultVO.getMembershipInfo().getMspId()).isNotNull();

    }

    @Test
    @DisplayName("멤버십_적립내역_저장")
    @Transactional
    public void saveAccumHis() throws Exception {
        // # 1. Given
        String barCd = createBarcode();
        String franchiseeId = "F230228000002";

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();

        myMembershipMapper.save(myMembershipVO);
        MyMspDetailInfoVO myMspDetailInfoVO = myMembershipMapper.findDetailByBarCdAndFranchiseeId(barCd, franchiseeId);
        MembershipVO membershipInfoVO = myMspDetailInfoVO.getMembershipInfo();
        MembershipFranchiseeVO franchiseeInfoVO = myMspDetailInfoVO.getFranchiseeInfo();
        MembershipGradeVO gradeBenefitInfoVO = myMspDetailInfoVO.getGradeBenefitInfo();

        String cancelBarCd = createBarcode();
        int tradeAmt = 10000;
        int accumRat = gradeBenefitInfoVO.getAccumRat();
        int accumPoint = NumberUtil.getCalcPerAmt(tradeAmt, accumRat);
        String expireYmd = DateUtil.getAddMonth(membershipInfoVO.getActiveMonths());

        MyMembershipAccumHisVO accumHisVO = MyMembershipAccumHisVO.builder()
                .cancelBarCd(cancelBarCd)
                .accountId(accountId)
                .mspId(mspId)
                .franchiseeId(franchiseeId)
                .tradeAmt(tradeAmt)
                .mspGradeCd(myMspDetailInfoVO.getMspGradeCd())
                .accumRat(accumRat)
                .accumPoint(accumPoint)
                .expireYmd(expireYmd)
                .build();

        // # 2. When
        myMembershipMapper.saveAccumHis(accumHisVO);
        MyMembershipAccumHisVO chkAccumHisVO = myMembershipMapper.findAccumHisById(cancelBarCd);

        // # 3. Then
        assertThat(chkAccumHisVO).isNotNull();
        assertThat(chkAccumHisVO.getCancelBarCd()).isEqualTo(cancelBarCd);
        assertThat(chkAccumHisVO.getAccumPoint()).isEqualTo(accumPoint);
        assertThat(chkAccumHisVO.getStatus()).isEqualTo(Code.STATUS_USE);
        assertThat(chkAccumHisVO.getAccumDate()).isNotNull();
    }


    public String createBarcode() throws Exception {
        StringBuilder barcode = new StringBuilder();
        barcode.append(DateUtil.nowDateToString());
        barcode.append(String.format("%06d", myMembershipMapper.getBarcodeSeq()));

        return barcode.toString();
    }

}
