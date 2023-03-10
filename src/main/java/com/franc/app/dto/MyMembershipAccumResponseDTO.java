package com.franc.app.dto;

import com.franc.app.code.Code;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MyMembershipAccumResponseDTO {
    private String resultCode;
    private String resultMessage;


    private Long accountId;
    private String mspId;
    private String mspNm;
    private String franchiseeId;
    private String franchiseeNm;
    private String mspGradeCd;
    private Integer accumRat;
    private Integer discRat;
    private Integer totalAccumPoint;
    private Integer tradeAmt;
    private Integer accumPoint;
    private Integer discAmt;


}
