package com.sam.canpoint.ecard.reveiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 分钟广播
 */
class TimeChangeReceiver : BroadcastReceiver() {

    private var callback: TimeChangeCallback? = null

    fun setTimeChangeCallback(callback: TimeChangeCallback?) {
        this.callback = callback
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_TIME_TICK) {
            callback?.timeChange()
        }
    }

    interface TimeChangeCallback {
        fun timeChange()
    }
}