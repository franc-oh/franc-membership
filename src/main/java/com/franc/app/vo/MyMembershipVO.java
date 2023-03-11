package com.franc.app.vo;

import com.franc.app.code.MembershipGrade;
import com.franc.app.code.Status;
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
    private Character status = Status.USE.getCode();

    @Builder.Default
    private Integer totalAccumPoint = 0;

    @Builder.Default
    private String mspGradeCd = MembershipGrade.COMMON.getCode();
    private LocalDateTime insertDate;
    private LocalDateTime withdrawalDate;
    private String barCd;


    public void setBarCd(String barCd) {
        this.barCd = barCd;
    }

}
