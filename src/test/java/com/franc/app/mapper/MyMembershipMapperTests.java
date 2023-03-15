package com.franc.app.mapper;

import com.franc.app.code.MembershipGrade;
import com.franc.app.code.Status;
import com.franc.app.util.DateUtil;
import com.franc.app.util.NumberUtil;
import com.franc.app.vo.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MyMembershipMapperTests {

    @Autowired
    private MyMembershipMapper myMembershipMapper;

    Long accountId = 5L;
    static String mspId = "M230227000001";

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
        assertThat(myMembership.getStatus()).isEqualTo(Status.WITHDRAWAL.getCode());
        assertThat(myMembership.getWithdrawalDate()).isNotNull();

    }

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
        assertThat(resultVO.getStatus()).isEqualTo(Status.USE.getCode());
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

        // # 2. When
        String cancelBarCd = saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 10000);
        MyMembershipAccumHisVO chkAccumHisVO = myMembershipMapper.findByIdAccumHis(cancelBarCd);

        // # 3. Then
        assertThat(chkAccumHisVO).isNotNull();
        assertThat(chkAccumHisVO.getCancelBarCd()).isEqualTo(cancelBarCd);
        assertThat(chkAccumHisVO.getStatus()).isEqualTo(Status.USE.getCode());
        assertThat(chkAccumHisVO.getAccumDate()).isNotNull();
    }

    @Test
    @DisplayName("멤버십별_적립총액_계산")
    @Transactional
    public void getMyMembershipTotalAccumPoint() throws Exception {
        // # 1. Given
        String franchiseeId = "F230228000002";
        String barCd = createBarcode();

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();
        myMembershipMapper.save(myMembershipVO);

        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 10000);
        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 10000);
        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 10000);

        // # 2. When
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("accountId", accountId);
        paramMap.put("mspId", mspId);
        int totalAccumPoint = myMembershipMapper.getMyMembershipTotalAccumPoint(paramMap);

        // # 3. Then
        assertThat(totalAccumPoint).isEqualTo(900);

    }

    @Test
    @DisplayName("총액에 따른 등급가져오기")
    @Transactional
    public void getMembershipGradeByPoint() throws Exception {
        // # 1. Given
        String franchiseeId = "F230228000002";
        String barCd = createBarcode();

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();
        myMembershipMapper.save(myMembershipVO);

        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 120000);

        Map<String, Object> getTotalAccumPointparamMap = new HashMap<>();
        getTotalAccumPointparamMap.put("accountId", accountId);
        getTotalAccumPointparamMap.put("mspId", mspId);
        int totalAccumPoint = myMembershipMapper.getMyMembershipTotalAccumPoint(getTotalAccumPointparamMap);


        // # 2. When
        Map<String, Object> getGradeParamMap = new HashMap<>();
        getGradeParamMap.put("point", totalAccumPoint);
        getGradeParamMap.put("mspId", mspId);
        MembershipGradeVO gradeVO = myMembershipMapper.getMembershipGradeByPoint(getGradeParamMap);

        // # 3. Then
        assertThat(gradeVO).isNotNull();
        assertThat(gradeVO.getMspId()).isEqualTo(mspId);
        assertThat(gradeVO.getMspGradeCd()).isEqualTo(MembershipGrade.SILVER.getCode());
    }

    @Test
    @DisplayName("멤버십_포인트_등급_변경")
    @Transactional
    public void updatePointAndGrade() throws Exception {
        // # 1. Given
        String franchiseeId = "F230228000002";
        String barCd = createBarcode();

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();
        myMembershipMapper.save(myMembershipVO);

        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 120000);

        Map<String, Object> getTotalAccumPointparamMap = new HashMap<>();
        getTotalAccumPointparamMap.put("accountId", accountId);
        getTotalAccumPointparamMap.put("mspId", mspId);
        int totalAccumPoint = myMembershipMapper.getMyMembershipTotalAccumPoint(getTotalAccumPointparamMap);

        Map<String, Object> getGradeParamMap = new HashMap<>();
        getGradeParamMap.put("point", totalAccumPoint);
        getGradeParamMap.put("mspId", mspId);
        MembershipGradeVO gradeVO = myMembershipMapper.getMembershipGradeByPoint(getGradeParamMap);
        String mspGradeCd = gradeVO.getMspGradeCd();

        MyMembershipVO updateMyMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .mspGradeCd(mspGradeCd)
                .totalAccumPoint(totalAccumPoint)
                .build();

        // # 2. When
        myMembershipMapper.updatePointAndGrade(updateMyMembershipVO);

        Map<String, Object> getFindByIdParamMap = new HashMap<>();
        getFindByIdParamMap.put("accountId", accountId);
        getFindByIdParamMap.put("mspId", mspId);
        MyMembershipVO resultMyMembershipVO = myMembershipMapper.findById(getFindByIdParamMap);

        // # 3. Then
        assertThat(resultMyMembershipVO).isNotNull();
        assertThat(resultMyMembershipVO.getAccountId()).isEqualTo(accountId);
        assertThat(resultMyMembershipVO.getMspId()).isEqualTo(mspId);
        assertThat(resultMyMembershipVO.getMspGradeCd()).isEqualTo(mspGradeCd);
        assertThat(resultMyMembershipVO.getTotalAccumPoint()).isEqualTo(totalAccumPoint);
    }

    @ParameterizedTest
    @MethodSource("findAllAccumHisParams")
    @DisplayName("적립내역_조회")
    @Transactional
    public void findAllAccumHis(Long accountId, String mspId, Character status, Integer pageNo, Integer pageLimit) throws Exception {
        // # 1. Given
        String franchiseeId = "F230228000002";
        String barCd = createBarcode();

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();
        myMembershipMapper.save(myMembershipVO);

        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 120000);
        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 15000);
        saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 500);

        MyMembershipAccumHisVO paramVO = MyMembershipAccumHisVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .status(status)
                .pageNo(pageNo)
                .pageLimit(pageLimit)
                .build();
        paramVO.setPaging();

        // # 2. When
        List<MyMembershipAccumHisVO> listVO = myMembershipMapper.findAllAccumHis(paramVO);

        // # 3. Then
        assertThat(listVO).isNotEmpty();
        assertThat(listVO.size()).isGreaterThan(0);
        assertThat(listVO.get(0).getStatus()).isEqualTo(Status.USE.getCode());
        assertThat(listVO.get(0).getMspNm()).isNotNull();
        assertThat(listVO.get(0).getFranchiseeNm()).isNotNull();
    }
    public static Stream<Arguments> findAllAccumHisParams() throws Exception {
        return Stream.of(
            Arguments.of(5L, mspId, null, 1, 1),
            Arguments.of(5L, mspId, Status.USE.getCode(), 1, 20),
            Arguments.of(5L, mspId, null, null, null)
        );
    }

    @Test
    @DisplayName("적립상세조회")
    @Transactional
    public void findByIdAccumHisDetail() throws Exception {
        // # 1. Given
        String franchiseeId = "F230228000002";
        String barCd = createBarcode();

        MyMembershipVO myMembershipVO = MyMembershipVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .barCd(barCd)
                .build();
        myMembershipMapper.save(myMembershipVO);

        String cancelBarCd = saveAccumHisByBarCdAndFranchiseeId(barCd, franchiseeId, 120000);

        // # 2. When
        MyMspAccumDetailInfoVO detailInfoVO = myMembershipMapper.findByIdAccumHisDetail(cancelBarCd);

        // # 3. Then
        assertThat(detailInfoVO).isNotNull();
        assertThat(detailInfoVO.getCancelBarCd()).isEqualTo(cancelBarCd);
        assertThat(detailInfoVO.getMembershipInfo()).isNotNull();
        assertThat(detailInfoVO.getMembershipInfo().getMspNm()).isNotNull();
        assertThat(detailInfoVO.getFranchiseeInfo()).isNotNull();
        assertThat(detailInfoVO.getFranchiseeInfo().getFranchiseeNm()).isNotNull();
        assertThat(detailInfoVO.getGradeBenefitInfo()).isNotNull();
        assertThat(detailInfoVO.getGradeBenefitInfo().getDiscRat()).isNotNull();
    }


    @Transactional
    public String saveAccumHisByBarCdAndFranchiseeId(String barCd, String franchiseeId, int tradeAmt) throws Exception {
        MyMspDetailInfoVO myMspDetailInfoVO = myMembershipMapper.findDetailByBarCdAndFranchiseeId(barCd, franchiseeId);

        MembershipVO membershipInfoVO = myMspDetailInfoVO.getMembershipInfo();
        MembershipFranchiseeVO franchiseeInfoVO = myMspDetailInfoVO.getFranchiseeInfo();
        MembershipGradeVO gradeBenefitInfoVO = myMspDetailInfoVO.getGradeBenefitInfo();

        String cancelBarCd = createBarcode();
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

        return cancelBarCd;
    }


    public String createBarcode() throws Exception {
        StringBuilder barcode = new StringBuilder();
        barcode.append(DateUtil.nowDateToString());
        barcode.append(String.format("%06d", myMembershipMapper.getBarcodeSeq()));

        return barcode.toString();
    }

}
