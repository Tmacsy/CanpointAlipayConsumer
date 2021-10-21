package com.sam.canpoint.ecard.api.request;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

@DatabaseTable(tableName = "local_record")
public class ConsumptionLocalRecordsRequest implements Parcelable {
    public final static String _arriveAmount = "arriveAmount";
    public final static String _consumeAmount = "consumeAmount";
    public final static String _consumeType = "consumeType";
    public final static String _discountAmount = "discountAmount";
    public final static String _isOnline = "isOnline";
    public final static String _schoolCode = "schoolCode";
    public final static String _sn = "sn";
    public final static String _timeStamp = "timeStamp";
    public final static String _userCode = "userCode";
    public final static String _isSynchronized = "isSynchronized";
    public final static String _zfbUid = "zfbUid";
    public final static String _faceToken = "faceToken";
    public final static String _scene = "scene";
    public final static String _orderId = "orderId";
    public final static String _businessChannel = "businessChannel";
    public final static String _uuidFlag = "uuidFlag";
    /**
     * arriveAmount : 0	应收金额(实收)
     * consumeAmount : 0	消费金额（实际）
     * consumeType :消费方式(1:刷卡,2:刷脸)
     * discountAmount : 0优惠金额
     * isOnline :	是否联机消费(0:脱机,1:联机)
     * schoolCode :	学校编码
     * sn :	设备编码
     * userCode :	用户唯一标识
     */

    @DatabaseField
    private BigDecimal arriveAmount;
    @DatabaseField
    private BigDecimal consumeAmount;
    @DatabaseField
    private BigDecimal discountAmount;
    @DatabaseField
    private String consumeType;
    @DatabaseField
    private String isOnline;//	是否联机消费("0":脱机,"1":联机)
    @DatabaseField
    private String schoolCode;
    @DatabaseField
    private String sn;
    @DatabaseField(id = true)
    private String timeStamp;
    @DatabaseField
    private String userCode;
    @DatabaseField
    private String userName;
    @DatabaseField
    private boolean isSynchronized;//是否同步（本地字段）
    @DatabaseField
    private String zfbUid;
    @DatabaseField
    private String faceToken;
    @DatabaseField
    private String scene;
    @DatabaseField
    private String terminalParams;
    @DatabaseField
    private String orderId;
    @DatabaseField
    private String businessChannel;//支付方式   1."EFACE_ALIPAY_FACE_BALANCE" 支付宝当面付之刷脸支付（扣除余额） 2."EFACE_ALIPAY_FACE" 支付宝当面付之刷脸支付（扣支付宝）   3."REFUND" 退款
    @DatabaseField
    private String uuidFlag; //订单唯一标识

    public ConsumptionLocalRecordsRequest() {
    }

    protected ConsumptionLocalRecordsRequest(Parcel in) {
        arriveAmount = new BigDecimal(in.readString());
        consumeAmount = new BigDecimal(in.readString());
        discountAmount = new BigDecimal(in.readString());
        consumeType = in.readString();
        isOnline = in.readString();
        schoolCode = in.readString();
        sn = in.readString();
        timeStamp = in.readString();
        userCode = in.readString();
        userName = in.readString();
        isSynchronized = in.readByte() != 0;
        zfbUid = in.readString();
        faceToken = in.readString();
        scene = in.readString();
        terminalParams = in.readString();
        orderId = in.readString();
        businessChannel = in.readString();
        uuidFlag = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUnNullString(arriveAmount + "", "0"));
        dest.writeString(getUnNullString(consumeAmount + "", "0"));
        dest.writeString(getUnNullString(discountAmount + "", "0"));
        dest.writeString(consumeType);
        dest.writeString(isOnline);
        dest.writeString(schoolCode);
        dest.writeString(sn);
        dest.writeString(timeStamp);
        dest.writeString(userCode);
        dest.writeString(userName);
        dest.writeByte((byte) (isSynchronized ? 1 : 0));
        dest.writeString(zfbUid);
        dest.writeString(faceToken);
        dest.writeString(scene);
        dest.writeString(terminalParams);
        dest.writeString(orderId);
        dest.writeString(businessChannel);
        dest.writeString(uuidFlag);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConsumptionLocalRecordsRequest> CREATOR = new Creator<ConsumptionLocalRecordsRequest>() {
        @Override
        public ConsumptionLocalRecordsRequest createFromParcel(Parcel in) {
            return new ConsumptionLocalRecordsRequest(in);
        }

        @Override
        public ConsumptionLocalRecordsRequest[] newArray(int size) {
            return new ConsumptionLocalRecordsRequest[size];
        }
    };

    public String getTerminalParams() {
        return terminalParams;
    }

    public void setTerminalParams(String terminalParams) {
        this.terminalParams = terminalParams;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public BigDecimal getArriveAmount() {
        return arriveAmount;
    }

    public void setArriveAmount(BigDecimal arriveAmount) {
        this.arriveAmount = arriveAmount;
    }

    public BigDecimal getConsumeAmount() {
        return consumeAmount;
    }

    public void setConsumeAmount(BigDecimal consumeAmount) {
        this.consumeAmount = consumeAmount;
    }

    public String getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(String consumeType) {
        this.consumeType = consumeType;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSynchronized() {
        return isSynchronized;
    }

    public void setSynchronized(boolean aSynchronized) {
        isSynchronized = aSynchronized;
    }

    public String getZfbUid() {
        return zfbUid;
    }

    public void setZfbUid(String zfbUid) {
        this.zfbUid = zfbUid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBusinessChannel() {
        return businessChannel;
    }

    public void setBusinessChannel(String businessChannel) {
        this.businessChannel = businessChannel;
    }

    public String getUuidFlag() {
        return uuidFlag;
    }

    public void setUuidFlag(String uuidFlag) {
        this.uuidFlag = uuidFlag;
    }

    // 为空判断
    private String getUnNullString(String s, String defaultData) {
        return (s == null || TextUtils.isEmpty(s) || "null".equals(s)) ? defaultData : s;
    }
}