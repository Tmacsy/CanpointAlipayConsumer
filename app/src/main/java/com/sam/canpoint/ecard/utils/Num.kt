package com.sam.canpoint.ecard.utils

import android.text.TextUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

fun String.checkPrice(): Boolean {
    try {
        if (this.endsWith(".")) return false
        val price = this.toDouble()
        if (price <= 0) return false
        val maxSingleConsume = CanPointSp.maxSingleConsume.toDouble()
        if (price > maxSingleConsume) return false
    } catch (e: Exception) {
        return false
    }
    return true
}


fun String?.mobile(): String {
    if (TextUtils.isEmpty(this) || this?.length != 11) return this ?: ""
    val sb = StringBuilder()
    val toCharArray = this.toCharArray()
    for (i in toCharArray.indices) {
        if (i in 3..6) {
            sb.append("*")
        } else {
            sb.append(toCharArray[i])
        }
    }
    return sb.toString()
}

fun Long.forMatTime(): String {
    val simpleDateFormat = SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault())
    val date2 = Date()
    date2.time = this
    return simpleDateFormat.format(date2)
}