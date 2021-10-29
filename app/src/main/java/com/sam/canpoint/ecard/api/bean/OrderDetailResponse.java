package com.sam.canpoint.ecard.api.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.math.BigDecimal;

public class OrderDetailResponse implements Parcelable {

    /**
     * consumeTime : 2021-07-15 17:42:01
     * arriveAmount : 0.01
     * orderId : CP202107131739055547754349381316750
     * userName : 蔡世玉
     * consumeStatus : 2
     */
    public String consumeTime;
    public BigDecimal arriveAmount;
    public String orderId;
    public String userName;
    public String consumeStatus;//消费状态(0:已提交,1:处理中,2:已成功,3:已失败,4:已退款)

    public OrderDetailResponse() {

    }

    protected OrderDetailResponse(Parcel in) {
        consumeTime = in.readString();
        arriveAmount = new BigDecimal(in.readString());
        orderId = in.readString();
        userName = in.readString();
        consumeStatus = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(consumeTime);
        dest.writeString(getUnNullString(arriveAmount + "", "0"));
        dest.writeString(orderId);
        dest.writeString(userName);
        dest.writeString(consumeStatus);
    }

    // 为空判断
    private String getUnNullString(String s, String defaultData) {
        return (s == null || TextUtils.isEmpty(s) || "null".equals(s)) ? defaultData : s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderDetailResponse> CREATOR = new Creator<OrderDetailResponse>() {
        @Override
        public OrderDetailResponse createFromParcel(Parcel in) {
            return new OrderDetailResponse(in);
        }

        @Override
        public OrderDetailResponse[] newArray(int size) {
            return new OrderDetailResponse[size];
        }
    };
}
