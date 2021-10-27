package com.sam.canpoint.ecard.utils

import java.lang.Exception

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