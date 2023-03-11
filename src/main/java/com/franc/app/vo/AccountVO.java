package com.franc.app.vo;

import com.franc.app.code.AccountGrade;
import com.franc.app.code.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "accountId")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountVO {

    private Long accountId;
    private String accountNm;

    @Builder.Default
    private Character status = Status.USE.getCode();
    private String birth;
    private String hphone;
    private String email;

    @Builder.Default
    private String accountGrade = AccountGrade.USER.getCode();
    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;
}
