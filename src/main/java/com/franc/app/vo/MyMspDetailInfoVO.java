package com.franc.app.vo;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MyMspDetailInfoVO extends MyMembershipVO {

    private MembershipVO membershipInfo;
    private MembershipFranchiseeVO franchiseeInfo;
    private MembershipGradeVO gradeBenefitInfo;
    private MyMembershipAccumHisVO procInfo;

}
