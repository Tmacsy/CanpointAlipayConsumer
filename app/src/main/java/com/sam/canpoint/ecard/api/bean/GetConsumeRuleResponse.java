package com.sam.canpoint.ecard.api.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;
import java.util.List;

public class GetConsumeRuleResponse {
    public final static String _searchValue = "searchValue";
    public final static String _createBy = "createBy";
    public final static String _createTime = "createTime";
    public final static String _updateBy = "updateBy";
    public final static String _updateTime = "updateTime";
    public final static String _remark = "remark";
    public final static String _id = "id";
    public final static String _everydayConsume = "everydayConsume";
    public final static String _singleConsume = "singleConsume";
    public final static String _debtAmount = "debtAmount";
    public final static String _preferType = "preferType";
    public final static String _discount = "discount";
    public final static String _remit = "remit";
    public final static String _isPaymentDeposit = "isPaymentDeposit";
    public final static String _depositAmount = "depositAmount";
    public final static String _paymentExplain = "paymentExplain";
    public final static String _offlinePopupNum = "offlinePopupNum";
    public final static String _offlineStopNum = "offlineStopNum";
    public final static String _schoolCode = "schoolCode";
    public final static String _linkDeviceNum = "linkDeviceNum";
    /**
     * searchValue : null
     * createBy :
     * createTime : null
     * updateBy :
     * updateTime : 2021-06-21 14:56:41
     * remark : null
     * params : {}
     * id : 10
     * everydayConsume : 11
     * singleConsume : 11
     * debtAmount : 11
     * preferType : 0
     * discount : null
     * remit : null
     * isPaymentDeposit : 1
     * depositAmount : 0.01
     * paymentExplain : 1212
     * offlinePopupNum: 10000
     * offlineStopNum: 50000
     * schoolCode : WHCANPOINT
     * linkDeviceNum : 1
     */
    //        @DatabaseField
    public String searchValue;
    @DatabaseField
    public String createBy;
    @DatabaseField
    public String createTime;
    @DatabaseField
    public String updateBy;
    //        @DatabaseField
    public String updateTime;
    //        @DatabaseField
    public String remark;
    @DatabaseField(id = true)
    public int id;
    @DatabaseField
    public BigDecimal everydayConsume;
    @DatabaseField
    public BigDecimal singleConsume;
    @DatabaseField
    public BigDecimal debtAmount;
    @DatabaseField
    public String preferType;
    @DatabaseField
    public BigDecimal discount;
    @DatabaseField
    public BigDecimal remit;
    @DatabaseField
    public String isPaymentDeposit;
    @DatabaseField
    public BigDecimal depositAmount;
    @DatabaseField
    public String paymentExplain;
    //        @DatabaseField
    public int offlinePopupNum;//脱机消费已达到N笔，请联系运维人员
    //        @DatabaseField
    public int offlineStopNum;//脱机消费已达到N笔，设备已停止使用，请联系运维人员。
    @DatabaseField
    public String schoolCode;
    @DatabaseField
    public int linkDeviceNum;
}
