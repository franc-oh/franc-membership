package com.franc.app.mapper;

import com.franc.app.vo.MembershipVO;
import com.franc.app.vo.MyMembershipVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=test", "jasypt.encryptor.password=franc_msp"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
