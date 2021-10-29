package com.sam.canpoint.ecard.api.bean;

import java.math.BigDecimal;

/**
 * 退款接口返回
 */
public class RefundResultBean {
    /**
     *
     */
    public BigDecimal refundPrice;
    public String refundName;
    public String orderId;
    public String createTime;
    public String refundResultStr;
}
