package com.sam.canpoint.ecard.utils

import com.sam.preference.SamSpManager

object CanPointSp {

    const val FREE_MODE = "1"  //自由  设备默认的消费模式  1自由 2定额
    const val QUOTA_MODE = "2" //定额
    const val VERIFY_FUNCTION = "verify_function"
    const val VERIFY_CHANGE_PASSWORD = "verify_change_password"
    const val VERIFY_EXIT_APP = "verify_exit_app"
    const val FACE_CONSUMPTION = "2" //刷脸消费
    const val OFFLINE_MODE = "0" //脱机消费（("0":脱机,"1":联机)）
    const val ONLINE_MODE = "1" //联机消费（("0":脱机,"1":联机)）
    const val OFFLINE = "offline" //offline（离线消费）


    private var config = SamSpManager.getInstance().preference()

    var authorization: String
        get() = config.getString("authorization", "")
        set(value) = config.setString("authorization", value)

    var serviceAddress: String
        get() = config.getString("service_addres", "https://tc.canpointlive.com/canpoint-eface")
        set(value) = config.setString("service_addres", value)

    var appRunCount: Int
        get() = config.getInt("first_run", 0)
        set(value) = config.setInt("first_run", value)

    var random_minute: Int
        get() = config.getInt("random_minute")
        set(value) = config.setInt("random_minute", value)

    var random_minute1: Int
        get() = config.getInt("random_minute1")
        set(value) = config.setInt("random_minute1", value)

    var random_minute2: Int
        get() = config.getInt("random_minute2")
        set(value) = config.setInt("random_minute2", value)

    var random_minute3: Int
        get() = config.getInt("random_minute3")
        set(value) = config.setInt("random_minute3", value)

    var schoolCode: String
        get() = config.getString("school_code", "CANPOINTLIVE")
        set(value) = config.setString("school_code", value)

    var schoolName: String
        get() = config.getString("school_name", "CANPOINTLIVE")
        set(value) = config.setString("school_name", value)

    var maxSingleConsume: String
        get() = config.getString("max_single_consume", "100")
        set(value) = config.setString("max_single_consume", value)

    var offlinePopupNum: Int
        get() = config.getInt("max_offline_popup_num")
        set(value) = config.setInt("max_offline_popup_num", value)

    var offlineStopNum: Int
        get() = config.getInt("max_offline_stop_num")
        set(value) = config.setInt("max_offline_stop_num", value)

    var password: String
        get() = config.getString("set_password", "888888")
        set(value) = config.setString("set_password", value)

    var iotStatus: Boolean
        get() = config.getBoolean("iot_status")
        set(value) = config.setBoolean("iot_status", value)

    var deviName: String
        get() = config.getString("device_name", "食堂人脸消费机03号")
        set(value) = config.setString("device_name", value)

    var patternType: String
        get() = config.getString("pattern_type", "1")
        set(value) = config.setString("pattern_type", value)

    var store: String
        get() = config.getString("store")
        set(value) = config.setString("store", value)

    var storeId: String
        get() = config.getString("store_id")
        set(value) = config.setString("store_id", value)

    var debtCount: Int
        get() = config.getInt("debt_count", 1)
        set(value) = config.setInt("debt_count", value)
}