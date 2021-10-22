package com.sam.canpoint.ecard.api.bean;

import java.util.List;

public class InitDeviceResponse {
    public AddDeviceBindResponse consumerMachine;

    public List<GetConsumeRuleResponse> consumeRuleList;

    public List<GetAccountInfoListResponse> userList;

    public String schoolName;
}
