package com.franc.app.vo;

import lombok.ToString;


@ToString(callSuper = true)
public class MspAndMyMspInfoVO extends MembershipVO {

    private String mspJoinYn;

    private MyMembershipVO mspJoinInfo;

}