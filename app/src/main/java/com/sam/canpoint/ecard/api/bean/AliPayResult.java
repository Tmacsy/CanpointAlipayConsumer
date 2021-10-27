package com.sam.canpoint.ecard.api.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

/**
 * 支付结果的liveData
 */
public class AliPayResult implements Parcelable {
    public String payName;
    public BigDecimal discountAmount; //折扣金额
    public BigDecimal arriveAmount; //实付金额
    public BigDecimal orderAmount;  //订单金额
    public String payResultStr;
    public int payResultCode;
    public String mobile;
    public long createTime;

    public AliPayResult(String payName, BigDecimal discountAmount, BigDecimal arriveAmount, BigDecimal orderAmount, String mobile, long createTime, String payResultStr, int payResultCode) {
        this.payName = payName;
        this.discountAmount = discountAmount;
        this.arriveAmount = arriveAmount;
        this.orderAmount = orderAmount;
        this.payResultStr = payResultStr;
        this.payResultCode = payResultCode;
        this.mobile = mobile;
        this.createTime = createTime;
    }

    protected AliPayResult(Parcel in) {
        payName = in.readString();
        payResultStr = in.readString();
        payResultCode = in.readInt();
        mobile = in.readString();
        createTime = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(payName);
        dest.writeString(payResultStr);
        dest.writeInt(payResultCode);
        dest.writeString(mobile);
        dest.writeLong(createTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AliPayResult> CREATOR = new Creator<AliPayResult>() {
        @Override
        public AliPayResult createFromParcel(Parcel in) {
            return new AliPayResult(in);
        }

        @Override
        public AliPayResult[] newArray(int size) {
            return new AliPayResult[size];
        }
    };
}
