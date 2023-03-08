package com.franc.app.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MembershipFindAllResponseDTO {
    private String resultCode;
    private String resultMessage;

    private Integer membershipCnt;
    private List<MembershipInfo> membershipList;


    @Data
    public static class MembershipInfo {
        private String mspId;
        private String mspNm;
        private String mspInfo;
        private String mspImgUrl;
        private String joinYn;
    }
}
