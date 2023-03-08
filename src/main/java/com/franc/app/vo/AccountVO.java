package com.franc.app.vo;

import com.franc.app.code.Code;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = "accountId")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountVO {

    private Long accountId;
    private String accountNm;

    @Builder.Default
    private Character status = Code.STATUS_USE;
    private String birth;
    private String hphone;
    private String email;

    @Builder.Default
    private String accountGrade = Code.ACCOUNT_GRADE_USER;
    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;
}
