package com.sam.canpoint.ecard.api.bean;

import java.util.ArrayList;

public class SplashZipData {
    public ArrayList<GetConsumeRuleResponse> list;
    public AddDeviceBindResponse addDeviceBindResponse;
    public MerchantInfoBean merchantInfoBean;

    public SplashZipData(ArrayList<GetConsumeRuleResponse> list, AddDeviceBindResponse addDeviceBindResponse, MerchantInfoBean infoBean) {
        this.list = list;
        this.addDeviceBindResponse = addDeviceBindResponse;
        merchantInfoBean = infoBean;
    }
}
