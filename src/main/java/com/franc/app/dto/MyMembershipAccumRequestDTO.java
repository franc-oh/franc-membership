package com.franc.app.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyMembershipAccumRequestDTO {
    @NotNull
    @Min(1)
    private String barCd;

    @NotNull
    private String franchisseId;

    @NotNull
    @Min(0)
    private Integer tradeAmt;


}
