package com.franc.app.vo;

import com.franc.app.code.Code;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode(of = {"mspId"})
@NoArgsConstructor
public class MembershipVO {

    private String mspId;
    private String mspNm;
    private Character status = Code.STATUS_USE;
    private String mspInfo;
    private String mspImgUrl;
    private String homepageUrl;
    private Integer activeMonths = 3;
    private String bigo;
    private LocalDateTime insertDate;
    private String insertUser;
    private LocalDateTime updateDate;
    private String updateUser;


    // 조회 관련 필드
    private String joinYn;


}
