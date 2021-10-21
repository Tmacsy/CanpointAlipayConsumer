package com.sam.canpoint.ecard.manager

import com.alipay.iot.sdk.APIManager
import java.lang.Exception

class VoiceManager {

    companion object {
        private var instance: VoiceManager? = null
            get() {
                if (field == null) {
                    field = VoiceManager()
                }
                return field
            }

        @Synchronized
        fun get(): VoiceManager {
            return instance!!
        }
    }

    fun voice(code: String) {
        try {
            val voiceApi = APIManager.getInstance().voiceAPI
            voiceApi.play(code)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}