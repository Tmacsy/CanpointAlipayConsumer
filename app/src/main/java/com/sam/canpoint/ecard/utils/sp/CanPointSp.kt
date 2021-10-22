package com.sam.canpoint.ecard.utils.sp

import com.sam.preference.SamSpManager

object CanPointSp {

    const val FREE_MODE = "1"  //自由  设备默认的消费模式  1自由 2定额
    const val QUOTA_MODE = "2" //定额

    private var config: ICanPointConfig =
            SamSpManager.getInstance().preference(ICanPointConfig::class.java)

    var authorization: String
        get() = config.getAuthorization()
        set(value) = config.setAuthorization(value)

    var serviceAddress: String
        get() = config.getServiceAddress()
        set(value) = config.setServiceAddress(value)

    var appRunCount: Int
        get() = config.getAppRunCount()
        set(value) = config.setAppRunCount(value)

    var random_minute: Int
        get() = config.getRandomMinute()
        set(value) = config.setRandomMinute(value)

    var random_minute1: Int
        get() = config.getRandomMinute1()
        set(value) = config.setRandomMinute1(value)

    var random_minute2: Int
        get() = config.getRandomMinute2()
        set(value) = config.setRandomMinute2(value)

    var random_minute3: Int
        get() = config.getRandomMinute3()
        set(value) = config.setRandomMinute3(value)

    var schoolCode: String
        get() = config.getSchoolCode()
        set(value) = config.setSchoolCode(value)

    var schoolName: String
        get() = config.getSchoolName()
        set(value) = config.setSchoolName(value)

    var maxSingleConsume: String
        get() = config.getMaxSingleConsume()
        set(value) = config.setMaxSingleConsume(value)

    var offlinePopupNum: Int
        get() = config.getOfflinePopupNum()
        set(value) = config.setOfflinePopupNum(value)

    var offlineStopNum: Int
        get() = config.getOfflineStopNum()
        set(value) = config.setOfflineStopNum(value)

    var password: String
        get() = config.getPassword()
        set(value) = config.setPassword(value)

    var iotStatus: Boolean
        get() = config.getIotStatus()
        set(value) = config.setIotStatus(value)

    var deviName: String
        get() = config.getDeviceName()
        set(value) = config.setDeviceName(value)

    var patternType: String
        get() = config.getPatternType()
        set(value) = config.setPatternType(value)

    var store: String
        get() = config.getStore()
        set(value) = config.setStore(value)

    var storeId: String
        get() = config.getStoreId()
        set(value) = config.setStoreId(value)
}