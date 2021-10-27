package com.sam.canpoint.ecard.api.bean;

import java.math.BigDecimal;

/**
 * 支付接口返回
 */
public class PayResultBean {
    /**
     * arriveAmount : 0.01
     * orderSuccTime : null
     * orderId : CP202107051634045330929625592082555
     * orderDescribe : 蔡世玉老师-全品学生超市就餐-2021-07-05 16:34:04
     * paySerial :
     * discountAmount : 0
     * remark :
     * source : 支付宝消费机
     * userName : 蔡世玉
     * userCode : P15eicpkd49whh
     * modifyTime : null
     * orderAmount : 0.01
     * payAmount : 0.01
     * createTime : 1625474044681
     * deleteStatus : 0
     * payChannel : ALIPAY_FACE
     * payStatus : 0
     * status : 0
     * businessChannel : EFACE_ALIPAY_FACE_BALANCE
     */
    public BigDecimal arriveAmount;
    public long orderSuccTime;
    public String orderId;
    public String orderDescribe;
    public String paySerial;
    public BigDecimal discountAmount;
    public String remark;
    public String source;
    public String userName;
    public String userCode;
    public long modifyTime;
    public BigDecimal orderAmount;
    public BigDecimal payAmount;
    public long createTime;
    public String deleteStatus;
    public String payChannel;
    public String payStatus;
    public String status;
    public String mobile;
    public String businessChannel;
}
