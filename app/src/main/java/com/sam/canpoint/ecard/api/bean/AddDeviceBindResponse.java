package com.sam.canpoint.ecard.api.bean;

public class AddDeviceBindResponse {

    /**
     * msg : 操作成功
     * code : 200
     * data : {"searchValue":null,"createBy":"","createTime":"2020-12-30 12:05:02","updateBy":"","updateTime":null,"remark":null,"params":{},"id":66,"deviceName":"44444444","password":"888888","deviceType":"7","sn":"1111111","consumerModel":"1","status":"1","ipAddress":"127.0.0.1","macAddress":"123213","storeId":"4841017446464817448","isOpen":"1","firmwareVersion":null,"softVersion":null,"schoolCode":"CANPOINTLIVE","token":"b4db8901-bdee-4145-9a81-33ca4e56d52e","itemId":null,"supplierSn":null,"storeName":"古天乐小学第一食堂","authorization":"b4db8901-bdee-4145-9a81-33ca4e56d52e"}
     */


    public Object searchValue;
    public String createBy;
    public String createTime;
    public String updateBy;
    public Object updateTime;
    public Object remark;
    public ParamsDataEntity params;
    public int id;
    public String deviceName;
    public String password;
    public String deviceType;
    public String sn;
    public String consumerModel;
    public String status;
    public String ipAddress;
    public String macAddress;
    public String storeId;
    public String isOpen;
    public Object firmwareVersion;
    public Object softVersion;
    public String schoolCode;
    public String token;
    public Object itemId;
    public Object supplierSn;
    public String storeName;
    public String authorization;

    public static class ParamsDataEntity {
    }
}
