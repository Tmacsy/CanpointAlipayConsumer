package com.sam.canpoint.ecard.utils.sp

import com.sam.preference.annotations.PreferenceFile
import com.sam.preference.annotations.PreferenceGet
import com.sam.preference.annotations.PreferenceSet

@PreferenceFile
interface ICanPointConfig {

    @PreferenceGet(key = "authorization", defaultValue = "")
    fun getAuthorization(): String

    @PreferenceSet(key = "authorization")
    fun setAuthorization(value: String)

    @PreferenceGet(key = "first_run")
    fun getAppRunCount(): Int

    @PreferenceSet(key = "first_run")
    fun setAppRunCount(value: Int)

    @PreferenceGet(key = "random_minute")
    fun getRandomMinute(): Int

    @PreferenceSet(key = "random_minute")
    fun setRandomMinute(value: Int)

    @PreferenceGet(key = "random_minute1")
    fun getRandomMinute1(): Int

    @PreferenceSet(key = "random_minute1")
    fun setRandomMinute1(value: Int)

    @PreferenceGet(key = "random_minute2")
    fun getRandomMinute2(): Int

    @PreferenceSet(key = "random_minute2")
    fun setRandomMinute2(value: Int)

    @PreferenceGet(key = "random_minute3")
    fun getRandomMinute3(): Int

    @PreferenceSet(key = "random_minute3")
    fun setRandomMinute3(value: Int)

    @PreferenceGet(key = "school_code", defaultValue = "CANPOINTLIVE")
    fun getSchoolCode(): String

    @PreferenceSet(key = "school_code")
    fun setSchoolCode(value: String)

    @PreferenceGet(key = "max_single_consume", defaultValue = "100")
    fun getMaxSingleConsume(): String

    @PreferenceSet(key = "max_single_consume")
    fun setMaxSingleConsume(value: String)

    @PreferenceGet(key = "max_offline_popup_num") // 脱机消费已达到N笔，请联系运维人员 默认1000
    fun getOfflinePopupNum(): Int

    @PreferenceSet(key = "max_offline_popup_num")
    fun setOfflinePopupNum(value: Int)

    @PreferenceGet(key = "max_offline_stop_num") // 脱机消费已达到N笔，设备已停止使用，请联系运维人员 默认10000
    fun getOfflineStopNum(): Int

    @PreferenceSet(key = "max_offline_stop_num")
    fun setOfflineStopNum(value: Int)

    @PreferenceGet(key = "set_password",defaultValue = "888888")
    fun getPassword():String

    @PreferenceSet(key = "set_password")
    fun setPassword(value: String)

    @PreferenceGet(key = "iot_status")
    fun getIotStatus():Boolean

    @PreferenceSet(key = "iot_status")
    fun setIotStatus(value: Boolean)
}