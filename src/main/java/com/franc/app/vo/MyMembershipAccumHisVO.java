package com.franc.app.vo;


import com.franc.app.code.Code;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = {"cancelBarCd"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyMembershipAccumHisVO {

    private String cancelBarCd;

    @Builder.Default
    private Character status = Code.STATUS_USE;

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

}
