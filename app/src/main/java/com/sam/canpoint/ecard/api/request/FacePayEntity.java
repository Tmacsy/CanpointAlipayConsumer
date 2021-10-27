package com.sam.canpoint.ecard.api.request;

import java.math.BigDecimal;

public class FacePayEntity {
    public BigDecimal consumeAmount;
    public String faceToken;
    public String scene;
    public String schoolCode;
    public String uid;
    public String isOnline;
    public String consumeType;
    public String sn;
    public String timeStamp;
    public String uuidFlag;//订单唯一标识
    public String sign;
    public String terminalParams;
}
