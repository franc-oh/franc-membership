package com.franc.app.vo;

import com.franc.app.code.Code;
import com.franc.app.util.PageUtil;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = {"mspId"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MembershipVO {

    private String mspId;
    private String mspNm;

    @Builder.Default
    private Character status = Code.STATUS_USE;
    private String mspInfo;
    private String mspImgUrl;
    private String homepageUrl;

    @Builder.Default
    private Integer activeMonths = Code.MEMBERSHIP_ACTIVE_MONTHS;
    private String bigo;
    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;


    // 조회 인자
    private String joinYn;
    private Long accountId;
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
