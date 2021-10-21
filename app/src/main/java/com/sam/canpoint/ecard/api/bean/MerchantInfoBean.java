package com.sam.canpoint.ecard.api.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "MerchantInfo")
public class MerchantInfoBean {
    public final static String _isvPid = "isvPid";
    public final static String _isvName = "isvName";
    public final static String _merchantId = "merchantId";
    public final static String _merchantName = "merchantName";
    public final static String _merchantPayPid = "merchantPayPid";
    public final static String _deviceNum = "deviceNum";
    public final static String _groupID = "groupID";
    /**
     * deviceNum : SMIT1D2020B29005065
     * merchantId : 2088502794892350
     * appId : 2021002117657865
     * alipayStoreCode : TEST
     * partnerId : 2088502794892350
     * storeCode : TEST
     */
    @DatabaseField
    @JSONField(name = "isvPid")
    public String isvPid;
    @DatabaseField
    @JSONField(name = "isvName")
    public String isvName;
    @DatabaseField
    @JSONField(name = "merchantId")
    public String merchantId;
    @DatabaseField
    @JSONField(name = "merchantName")
    public String merchantName;
    @DatabaseField
    @JSONField(name = "merchantPayPid")
    public String merchantPayPid;
    @DatabaseField(id = true)
    @JSONField(name = "deviceNum")
    public String deviceNum;
    @DatabaseField
    @JSONField(name = "groupID")
    public String groupID;
}
