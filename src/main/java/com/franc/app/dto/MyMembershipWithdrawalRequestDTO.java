package com.franc.app.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyMembershipWithdrawalRequestDTO {
    @NotNull
    @Min(1)
    private Long accountId;

    @NotNull
    private String mspId;

}
