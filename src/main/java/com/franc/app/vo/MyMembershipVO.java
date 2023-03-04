package com.franc.app.vo;

import com.franc.app.code.Code;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = {"accountId", "mspId"})
@NoArgsConstructor
public class MyMembershipVO {

    private Long accountId;
    private String mspId;
    private Character status = Code.STATUS_USE;
    private Integer totalAccumPoint = 0;
    private String mspGradeCd = Code.MEMBERSHIP_GRADE_COMMON;
    private LocalDateTime insertDate;
    private LocalDateTime withdrawalDate;
    private String barCd;


    public void setBarCd(String barCd) {
        this.barCd = barCd;
    }

    @Builder
    public MyMembershipVO(Long accountId, String mspId, Character status, Integer totalAccumPoint, String mspGradeCd, String barCd, LocalDateTime withdrawalDate) {
        this.accountId = accountId;
        this.mspId = mspId;

        if(status != null)
            this.status = status;

        if(totalAccumPoint != null)
            this.totalAccumPoint = totalAccumPoint;

        if(mspGradeCd != null)
            this.mspGradeCd = mspGradeCd;

        if(barCd != null)
            this.barCd = barCd;

        if(withdrawalDate != null)
            this.withdrawalDate = withdrawalDate;
    }
}
