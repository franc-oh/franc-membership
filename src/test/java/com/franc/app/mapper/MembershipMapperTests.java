package com.franc.app.mapper;

import com.franc.app.vo.MembershipVO;
import com.franc.app.vo.MspAndMyMspInfoVO;
import com.franc.app.vo.MyMembershipVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class MembershipMapperTests {

    @Autowired
    private MembershipMapper membershipMapper;

    @Autowired
    private MyMembershipMapper myMembershipMapper;


    private Long accountId = 5L;
    private String mspId = "M230227000002";

    @Test
    @DisplayName("멤버십_전체조회_나의정보포함_필터링X")
    public void findAllAndMyInfo_no_filtering() throws Exception {
        // # 1. Given
        String joinYn = "";

        // # 2. When
        List<MembershipVO> list = membershipMapper.findAllOrMyMspList(MembershipVO.builder()
                .accountId(accountId)
                .joinYn(joinYn)
                .build());

        // # 3. Then
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("멤버십_전체조회_나의정보포함_필터링")
    @Transactional
    public void findAllAndMyInfo_filtering() throws Exception {
        // # 1. Given
        String joinYn = "Y";

        insertMyMembership(accountId, mspId); // 1건 등록

        // # 2. When
        List<MembershipVO> list = membershipMapper.findAllOrMyMspList(MembershipVO.builder()
                .accountId(accountId)
                .joinYn(joinYn)
                .build());

        // # 3. Then
        assertThat(list).isNotEmpty();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0).getMspId()).isEqualTo(mspId);
        assertThat(list.get(0).getJoinYn()).isEqualTo("Y");

    }

    @Test
    @DisplayName("멤버십_전체조회_나의정보포함_페이징")
    public void findAllAndMyInfo_paging() throws Exception {
        // # 1. Given
        String joinYn = "";
        int page_no = 2;
        int page_limit = 20;
        int offset = Math.abs(page_no -1) * page_limit;
        int limit = page_limit;

        MembershipVO paramVo = MembershipVO.builder()
                .accountId(accountId)
                .joinYn(joinYn)
                .pageNo(page_no)
                .pageLimit(page_limit)
                .build();
        paramVo.setPaging();

        // # 2. When
        List<MembershipVO> list = membershipMapper.findAllOrMyMspList(paramVo);

        // # 3. Then
        assertThat(list).isEmpty();
        //assertThat(list).isNotEmpty();
        //assertThat(list.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("멤버십_상세조회_데이터_없을때")
    public void findByIdAndMyInfo_null() throws Exception {
        // # 1. Given
        MspAndMyMspInfoVO paramVo = MspAndMyMspInfoVO.builder()
                .accountId(accountId)
                .mspId("1222")
                .build();


        // # 2. When
        MspAndMyMspInfoVO infoVo = membershipMapper.findByIdAndMyMspInfo(paramVo);

        // # 3. Then
        assertThat(infoVo).isNull();
    }

    @Test
    @DisplayName("멤버십_상세조회_데이터_있음_가입정보_없음")
    public void findByIdAndMyInfo_success_myMsp_is_null() throws Exception {
        // # 1. Given
        MspAndMyMspInfoVO paramVo = MspAndMyMspInfoVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .build();

        // # 2. When
        MspAndMyMspInfoVO infoVo = membershipMapper.findByIdAndMyMspInfo(paramVo);

        // # 3. Then
        assertThat(infoVo).isNotNull();
        assertThat(infoVo.getMspId()).isEqualTo(mspId);
        assertThat(infoVo.getMyMspInfo()).isNull();
        
    }

    @Test
    @DisplayName("멤버십_상세조회_데이터_있음_가입정보_있음")
    @Transactional
    public void findByIdAndMyInfo_success_myMsp_is_notNull() throws Exception {
        // # 1. Given
        MspAndMyMspInfoVO paramVo = MspAndMyMspInfoVO.builder()
                .accountId(accountId)
                .mspId(mspId)
                .build();

        insertMyMembership(accountId, mspId);

        // # 2. When
        MspAndMyMspInfoVO infoVo = membershipMapper.findByIdAndMyMspInfo(paramVo);

        // # 3. Then
        assertThat(infoVo).isNotNull();
        assertThat(infoVo.getMspId()).isEqualTo(mspId);
        assertThat(infoVo.getMyMspInfo()).isNotNull();
        assertThat(infoVo.getMyMspInfo().getAccountId()).isEqualTo(accountId);
        assertThat(infoVo.getMyMspInfo().getBarCd()).isNotNull();

    }


    public void insertMyMembership(Long accountId, String mspId) throws Exception {
        String barcode = "123456789";
        String mspGradeCd = "COMMON";
        MyMembershipVO myMembershipVo = new MyMembershipVO().builder()
                .accountId(accountId)
                .mspId(mspId)
                .mspGradeCd(mspGradeCd)
                .barCd(barcode)
                .build();

        // # When
        myMembershipMapper.save(myMembershipVo);
    }
}
