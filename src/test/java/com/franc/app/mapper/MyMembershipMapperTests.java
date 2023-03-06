package com.franc.app.mapper;

import com.franc.app.util.DateUtil;
import com.franc.app.vo.MyMembershipVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    public String createBarcode() throws Exception {
        StringBuilder barcode = new StringBuilder();
        barcode.append(DateUtil.nowDateToString());
        barcode.append(String.format("%06d", myMembershipMapper.getBarcodeSeq()));

        return barcode.toString();
    }

}
