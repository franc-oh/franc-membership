package com.franc.app.membership.vo;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "accountId")
@NoArgsConstructor
public class AccountVo {

    private Long accountId;
    private String accountNm;
    private Character status;
    private String birth;
    private String hphone;
    private String email;
    private String accountGrade;
    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;

    @Builder
    public AccountVo(Long accountId, String accountNm, Character status, String birth, String hphone, String email, String accountGrade, String insertUser, String updateUser) {
        this.accountId = accountId;
        this.accountNm = accountNm;
        this.status = status;
        this.birth = birth;
        this.hphone = hphone;
        this.email = email;
        this.accountGrade = accountGrade;
        this.insertUser = insertUser;
        this.updateUser = updateUser;
    }
}
