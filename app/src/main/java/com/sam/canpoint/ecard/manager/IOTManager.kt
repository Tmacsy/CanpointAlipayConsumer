package com.sam.canpoint.ecard.manager

import android.annotation.SuppressLint
import com.alipay.iot.sdk.APIManager
import com.sam.canpoint.ecard.application.CanPointECardApplication
import java.lang.Exception
import java.lang.reflect.InvocationTargetException

class IOTManager {

    companion object {
        private var instance: IOTManager? = null
            get() {
                if (field == null) {
                    field = IOTManager()
                }
                return field
            }

        @Synchronized
        fun get(): IOTManager {
            return instance!!
        }
    }

    fun voice(code: String) {
        voice(code, null)
    }

    fun voice(code: String, num: String?) {
        try {
            val voiceApi = APIManager.getInstance().voiceAPI
            if (num.isNullOrEmpty()) {
                voiceApi.play(code)
            } else {
                voiceApi.play(code, num)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    fun showLed(status: String, showMode: Int) {
        try {
            val antManager: Any = CanPointECardApplication.the().getSystemService("antservice")
            val antManagerClass: Class<*> = antManager.javaClass
            val requestLedStatus = antManagerClass.getDeclaredMethod("requestLedStatus", String::class.java, Int::class.java)
            requestLedStatus.invoke(antManager, status, showMode)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}