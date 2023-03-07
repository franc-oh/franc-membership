package com.franc.app.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MyMembershipWithdrawalResponseDTO {
    private String resultCode;
    private String resultMessage;
}
