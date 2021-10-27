package com.sam.canpoint.ecard.api.bean;

import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse;

public class SmileResult {
    public Smile2PayResponse smile2PayResponse;
    public int resultCode;
    public String inputPrice;

    public SmileResult(Smile2PayResponse smile2PayResponse, int resultCode, String inputPrice) {
        this.smile2PayResponse = smile2PayResponse;
        this.resultCode = resultCode;
        this.inputPrice = inputPrice;
    }

    public SmileResult() {
    }
}

