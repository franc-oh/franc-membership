package com.franc.app.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class MspAndMyMspInfoVO extends MembershipVO {

    private String mspJoinYn;

    private MyMembershipVO mspJoinInfo;

}
