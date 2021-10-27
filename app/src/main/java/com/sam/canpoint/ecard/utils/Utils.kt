package com.sam.canpoint.ecard.utils

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.IPayApi
import com.sam.http.SamApiManager
import com.sam.utils.device.DeviceUtils
import com.tencent.bugly.crashreport.CrashReport
import java.util.*

object Utils {

    fun resetFourRandomMinute() {
        val random = Random()
        CanPointSp.random_minute = random.nextInt(60)
        CanPointSp.random_minute1 = random.nextInt(60)
        CanPointSp.random_minute2 = random.nextInt(60)
        CanPointSp.random_minute3 = random.nextInt(60)
    }

    /**
     * 更换请求头中的Authorization
     */
    fun updateAuthorization(authorization: String) {
        updateAuthorization(authorization, ICanPointApi::class.java)
        updateAuthorization(authorization, IPayApi::class.java)
    }

    private fun <T> updateAuthorization(authorization: String, apiClazz: Class<T>) {
        val serviceConfig = SamApiManager.getInstance().getServiceConfig(apiClazz)
                .setAuthorization(authorization)
        SamApiManager.getInstance().setService(apiClazz, serviceConfig)
    }


    fun nextInt(begin: Long, end: Long): Long {
        return begin + (Math.random() * (end - begin)).toLong()
    }

    fun postCatchException(error: String) {
        CrashReport.postCatchedException(Throwable(error))
    }
}