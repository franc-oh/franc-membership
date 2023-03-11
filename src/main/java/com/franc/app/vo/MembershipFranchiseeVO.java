package com.franc.app.vo;

import com.franc.app.code.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = {"mspId", "franchiseeId"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MembershipFranchiseeVO {

    private String mspId;
    private String franchiseeId;
    private String franchiseeNm;
    @Builder.Default
    private Character status = Status.USE.getCode();

    private String zipCd;
    private String addr1;
    private String addr2;
    private String telNo;
    private String bigo;

    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;



}
