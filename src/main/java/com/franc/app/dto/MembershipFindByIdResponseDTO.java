package com.franc.app.dto;

import com.franc.app.code.Code;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MembershipFindByIdResponseDTO {
    private String resultCode;
    private String resultMessage;

    private String mspId;
    private String mspNm;

    private Character status;
    private String mspInfo;
    private String mspImgUrl;
    private String homepageUrl;
    private Integer activeMonths;
    private String bigo;


    private MyMembershipInfo myMembershipInfo;


    @Data
    public static class MyMembershipInfo {
        private Long accountId;
        private String mspId;
        private Character status;
        private Integer totalAccumPoint;
        private String mspGradeCd;
        private LocalDateTime insertDate;
        private LocalDateTime withdrawalDate;
        private String barCd;
    }
}
