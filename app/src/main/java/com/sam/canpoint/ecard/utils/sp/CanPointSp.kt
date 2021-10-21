package com.sam.canpoint.ecard.utils.sp

import com.sam.preference.SamSpManager

object CanPointSp {

    private var config: ICanPointConfig =
            SamSpManager.getInstance().preference(ICanPointConfig::class.java)

    var authorization: String
        get() = config.getAuthorization()
        set(value) {
            config.setAuthorization(value)
        }

    var appRunCount: Int
        get() = config.getAppRunCount()
        set(value) {
            config.setAppRunCount(value)
        }

    var random_minute: Int
        get() = config.getRandomMinute()
        set(value) {
            config.setRandomMinute(value)
        }
    var random_minute1: Int
        get() = config.getRandomMinute1()
        set(value) {
            config.setRandomMinute1(value)
        }
    var random_minute2: Int
        get() = config.getRandomMinute2()
        set(value) {
            config.setRandomMinute2(value)
        }
    var random_minute3: Int
        get() = config.getRandomMinute3()
        set(value) {
            config.setRandomMinute3(value)
        }

    var schoolCode: String
        get() = config.getSchoolCode()
        set(value) {
            config.setSchoolCode(value)
        }

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
}