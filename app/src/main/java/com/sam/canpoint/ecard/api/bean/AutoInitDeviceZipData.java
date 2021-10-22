package com.sam.canpoint.ecard.api.bean;

public class AutoInitDeviceZipData {
    public InitDeviceResponse initDeviceResponse;
    public MerchantInfoBean merchantInfoBean;

    public AutoInitDeviceZipData(InitDeviceResponse initDeviceResponse, MerchantInfoBean merchantInfoBean) {
        this.initDeviceResponse = initDeviceResponse;
        this.merchantInfoBean = merchantInfoBean;
    }
}
