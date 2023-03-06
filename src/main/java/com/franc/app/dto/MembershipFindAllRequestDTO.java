package com.franc.app.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MembershipFindAllRequestDTO {
    @NotNull
    @Min(1)
    private Long accountId;

    private String joinYn;

    private Integer pageNo;
    private Integer pageLimit;

}
