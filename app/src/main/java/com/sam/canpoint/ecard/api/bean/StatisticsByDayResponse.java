package com.sam.canpoint.ecard.api.bean;

import java.math.BigDecimal;

public class StatisticsByDayResponse {
    /**
     * actualAmount : 0.07
     * paymentCount : 7
     * paymentAmount : 0.07
     * refundCount : 1
     * refundAmount : 0
     * arriveAmount : 1
     * debtAmount : 0
     */
    public BigDecimal actualAmount;
    public int paymentCount;
    public BigDecimal paymentAmount;
    public int refundCount;
    public BigDecimal refundAmount;
    public BigDecimal arriveAmount;
    public BigDecimal debtAmount;
}
