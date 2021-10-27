package com.sam.canpoint.ecard.ui.model

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListResponse
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListTempResponse
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.utils.CanPointSp.schoolCode
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.http.SamApiManager
import com.sam.system.log.L
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ceil

class SyncAccountThread(@Volatile private var isInitData: Boolean = false) : Thread() {
    private val mCurrentPage = AtomicInteger(1)
    private val retryCount = AtomicInteger(1)
    private var callback: SyncAccountCallback? = null
    private var waitSyncAccountList = LinkedList<GetAccountInfoListResponse>()

    @Volatile
    private var total = -1

    override fun run() {
        super.run()
        val clearTable = SamDBManager.getInstance().dao(GetAccountInfoListTempResponse::class.java).clearTable()
        L.d("已清除${clearTable}条临时表数据!")
        while (!isInterrupted && (total == -1 || mCurrentPage.get() <= ceil(total / 30.0))) {
            try {
                syncAccount()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                interrupt()
            }
        }
        L.d("同步账户信息线程结束!")
        callback?.complete()
    }

    private fun syncAccount() {
        val execute = SamApiManager.getInstance().getService(ICanPointApi::class.java).getAccountInfoList(mCurrentPage.get(), 30, schoolCode).execute()
        if (execute.isSuccessful) {
            val body = execute.body()
            if (body != null && body.code == 200) {
                retryCount.set(0)
                total = body.total
                L.v("一共获取到${total}个账户信息")
                initData(body.rows)
            } else {
                retry()
            }
        } else {
            retry()
        }
    }

    private fun initData(d: ArrayList<GetAccountInfoListResponse>?) {
        if (d.isNullOrEmpty() || total == 0) {
            L.d("当前${mCurrentPage.get()}页账户信息为空!")
            callback?.error("当前${mCurrentPage.get()}页账户信息为空!")
            interrupt()
            return
        }
        L.d("获取第${mCurrentPage.get()}页账户信息成功")
        val incrementAndGet = mCurrentPage.incrementAndGet()
        L.d("接下来请求第:${incrementAndGet}页")
        if (isInitData) {
            //全量更新 成功一页更新保存一页
            for (response in d) {
                SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).addOrUpdate(response)
            }
            if (mCurrentPage.get() > ceil(total / 30.0)) {
                L.d("获取数据成功结束!")
                callback?.success(true)
            }
        } else {
            //增量更新 先将当前页数据存储   如果当前页本地已保存则同步已保存的数据列表
            waitSyncAccountList.addAll(d)
            for (response in d) {
                val exist = SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java)
                        .isExist(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._userCode, response.userCode))
                if (exist) {
                    syncData()
                    interrupt()
                    return
                }
            }
            if (mCurrentPage.get() > ceil(total / 30.0)) {
                syncData()
            }
        }
    }

    private fun retry() {
        val count = retryCount.get()
        if (count > 3) {
            callback?.error("获取第${count}页数据失败!")
            interrupt()
        } else {
            retryCount.incrementAndGet()
            syncAccount()
        }
    }

    private fun syncData() {
        if (isInitData) return
        for (rowsDataEntity in waitSyncAccountList) {
            SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).addOrUpdate(rowsDataEntity)
        }
        callback?.success(false)
    }

    fun setSyncCallback(callback: SyncAccountCallback?) {
        this.callback = callback
    }

    interface SyncAccountCallback {
        fun complete()
        fun success(isAll: Boolean)
        fun error(str: String)
    }
}