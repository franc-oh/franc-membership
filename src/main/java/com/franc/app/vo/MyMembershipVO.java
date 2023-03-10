package com.franc.app.vo;

import com.franc.app.code.Code;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter @Setter
@ToString
@EqualsAndHashCode(of = {"accountId", "mspId"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyMembershipVO {

    private Long accountId;
    private String mspId;

    @Builder.Default
    private Character status = Code.STATUS_USE;

    @Builder.Default
    private Integer totalAccumPoint = 0;

    @Builder.Default
    private String mspGradeCd = Code.MEMBERSHIP_GRADE_COMMON;
    private LocalDateTime insertDate;
    private LocalDateTime withdrawalDate;
    private String barCd;


    public void setBarCd(String barCd) {
        this.barCd = barCd;
    }

}
