package com.sam.canpoint.ecard.api.request;

public class AddDeviceBindRequest {

    /**
     * consumerModel :
     * createBy :
     * createTime :
     * deviceName :
     * deviceType :
     * firmwareVersion :
     * id : 0
     * ipAddress :
     * isOpen :
     * itemId :
     * macAddress :
     * params : {}
     * password :
     * remark :
     * schoolCode :
     * searchValue :
     * sn :
     * softVersion :
     * status :
     * storeId :
     * supplierSn :
     * token :
     * updateBy :
     * updateTime :
     * operateType :
     */

    public String consumerModel;
    public String createBy;
    public String createTime;
    public String deviceName;
    public String deviceType;
    public String firmwareVersion;
    public int id;
    public String ipAddress;
    public String isOpen;
    public String itemId;//型号信息
    public String macAddress;
    public ParamsData params;
    public String password;
    public String remark;
    public String schoolCode;
    public String searchValue;
    public String sn;
    public String softVersion;
    public String status;
    public String storeId;
    public String supplierSn;//厂商信息
    public String token;
    public String updateBy;
    public String updateTime;
    public String operateType;//操作类型（1：注册 2：查询）

    public static class ParamsData {
    }
}
