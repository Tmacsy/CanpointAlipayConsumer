package com.sam.canpoint.ecard.ui.model

/**
 * 扩展函数  按需实现回调
 */
inline fun SyncAccountThread.setCallback(
        crossinline complete: () -> Unit = {},
        crossinline success: (isAll: Boolean) -> Unit = { _ -> },
        crossinline error: (str: String) -> Unit = { _ -> },
): SyncAccountThread.SyncAccountCallback {
    val callback = object : SyncAccountThread.SyncAccountCallback {
        override fun complete() {
            complete.invoke()
        }

        override fun success(isAll: Boolean) {
            success.invoke(isAll)
        }

        override fun error(str: String) {
            error.invoke(str)
        }
    }
    setSyncCallback(callback)
    return callback
}