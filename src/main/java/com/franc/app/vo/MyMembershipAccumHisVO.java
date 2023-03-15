package com.franc.app.vo;


import com.franc.app.code.Status;
import com.franc.app.util.PageUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@ToString
@EqualsAndHashCode(of = {"cancelBarCd"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyMembershipAccumHisVO {

    private String cancelBarCd;

    @Builder.Default
    private Character status = Status.USE.getCode();

    private Long accountId;
    private String mspId;
    private String franchiseeId;
    private Integer tradeAmt;
    private String mspGradeCd;
    private Integer accumRat;
    private Integer accumPoint;
    private String expireYmd;
    private LocalDateTime accumDate;
    private LocalDateTime accumCancelDate;



    // 조회 인자
    private String mspNm;
    private String franchiseeNm;

    @Setter
    private String statusNm;
    private String expireYn;
    private Integer pageNo;
    private Integer pageLimit;

    @Builder.Default
    private Integer offset = 0;

    @Builder.Default
    private Integer limit = 20;


    public void setPaging() throws Exception {
        Map<String, Integer> pageMap = PageUtil.getPageMap(pageNo, pageLimit);
        offset = pageMap.get("offset");
        limit = pageMap.get("limit");
    }

}
