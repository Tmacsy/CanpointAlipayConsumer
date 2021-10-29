package com.sam.canpoint.ecard.api.bean;

public class LocalRecordBean {
    private String businessChannel;//支付方式   1."EFACE_ALIPAY_FACE_BALANCE" 支付宝当面付之刷脸支付（扣除余额） 2."EFACE_ALIPAY_FACE" 支付宝当面付之刷脸支付（扣支付宝）   3."REFUND" 退款
    private String name;
    private String time;
    private String orderAmount;
    private String actualAmount;
    private boolean isSynchronized;//是否同步
    private boolean isOffline;//是否离线消费
    private String orderId;//订单编号
    private boolean isSelected;//是否被选中

    public String getBusinessChannel() {
        return null == businessChannel ? "" : businessChannel;
    }

    public void setBusinessChannel(String businessChannel) {
        this.businessChannel = businessChannel;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(String actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
