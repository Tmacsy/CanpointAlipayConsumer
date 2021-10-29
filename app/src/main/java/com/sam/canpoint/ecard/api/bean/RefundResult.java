package com.sam.canpoint.ecard.api.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * 退款结果的liveData
 */
public class RefundResult implements Parcelable {
    public static final int CODE_SUCCESS = 200;
    public String refundName;
    public BigDecimal refundPrice;
    public String createTime;
    public String orderId;
    public String refundResultStr;
    public int refundResultCode;

    public RefundResult(String refundName, BigDecimal refundPrice, String createTime, String orderId, String refundResultStr, int refundResultCode) {
        this.refundName = refundName;
        this.refundPrice = refundPrice;
        this.createTime = createTime;
        this.orderId = orderId;
        this.refundResultStr = refundResultStr;
        this.refundResultCode = refundResultCode;
    }

    protected RefundResult(Parcel in) {
        refundName = in.readString();
        refundPrice = new BigDecimal(in.readString());
        createTime = in.readString();
        orderId = in.readString();
        refundResultStr = in.readString();
        refundResultCode = in.readInt();
    }

    public static final Creator<RefundResult> CREATOR = new Creator<RefundResult>() {
        @Override
        public RefundResult createFromParcel(Parcel in) {
            return new RefundResult(in);
        }

        @Override
        public RefundResult[] newArray(int size) {
            return new RefundResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(refundName);
        dest.writeString(getUnNullString(refundPrice + "", "0"));
        dest.writeString(createTime);
        dest.writeString(orderId);
        dest.writeString(refundResultStr);
        dest.writeInt(refundResultCode);
    }

    // 为空判断
    private String getUnNullString(String s, String defaultData) {
        return (s == null || TextUtils.isEmpty(s) || "null".equals(s)) ? defaultData : s;
    }
}
