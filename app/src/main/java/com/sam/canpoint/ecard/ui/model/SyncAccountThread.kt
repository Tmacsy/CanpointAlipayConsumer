package com.sam.canpoint.ecard.ui.model

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListResponse
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListTempResponse
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.utils.sp.CanPointSp.schoolCode
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.http.SamApiManager
import com.sam.system.log.L
import com.tyx.base.bean.BaseResponse
import com.tyx.base.mvvm.exception.RetryWithDelay
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseObserver
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.math.ceil

class SyncAccountThread(var isInitData: Boolean = false) : Thread() {
    private val mCurrentPage = AtomicInteger(1)
    private var callback: SyncAccountCallback? = null
    private var newAccountInfoList = LinkedList<GetAccountInfoListResponse>()

    @Volatile
    private var total = -1

    @Volatile
    private var isInitDate = isInitData

    override fun run() {
        super.run()
        val clearTable = SamDBManager.getInstance().dao(GetAccountInfoListTempResponse::class.java).clearTable()
        L.d("已清除${clearTable}条临时表数据!")
        while (!isInterrupted || (total != -1 && mCurrentPage.get() <= ceil(total / 30.0))) {
            try {
                syncAccount()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                interrupt()
            }
        }
        L.d("同步账户信息线程结束!")
        callback?.complete()
        callback = null
    }


    private fun syncAccount() {
        SamApiManager.getInstance().getService(ICanPointApi::class.java)
                .getAccountInfoList(mCurrentPage.get(), 30, schoolCode)
                .retryWhen(RetryWithDelay(3, 1000))
                .compose(applySchedulers(object : BaseObserver<ArrayList<GetAccountInfoListResponse>>() {
                    override fun getAllData(data: BaseResponse<ArrayList<GetAccountInfoListResponse>>?) {
                        total = data?.total ?: 0
                        L.v("一共获取到${total}个账户信息")
                    }

                    override fun onSuccess(d: ArrayList<GetAccountInfoListResponse>?, message: String?) {
                        initData(d)
                    }

                    override fun onFail(e: Throwable?) {
                        interrupt()
                    }
                }))
    }

    private fun initData(d: ArrayList<GetAccountInfoListResponse>?) {
        if (d.isNullOrEmpty() || total == 0) {
            L.d("当前${mCurrentPage.get()}页账户信息为空!")
            interrupt()
            return
        }
        L.d("获取第${mCurrentPage.get()}页账户信息成功")
        val incrementAndGet = mCurrentPage.incrementAndGet()
        L.d("接下来请求第:${incrementAndGet}页")
        if (isInitDate) {
            for (response in d) {
                SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).addOrUpdate(response)
            }
        } else {
            d.forEach {
                val exist = SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java)
                        .isExist(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._userCode, it.userCode))
                if (exist) {
                    for (rowsDataEntity in d) {
                        SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).addOrUpdate(rowsDataEntity)
                    }
                    interrupt()
                    return@forEach
                }
            }
            newAccountInfoList.addAll(d)
            if (mCurrentPage.get() > ceil(total / 30.0)) {
                L.e("开始存newAccountInfoList.size()=" + newAccountInfoList.size)
                //存到临时表
                for (rowsDataEntity in newAccountInfoList) {
                    val accountTempInfo = GetAccountInfoListTempResponse(rowsDataEntity)
                    SamDBManager.getInstance().dao(GetAccountInfoListTempResponse::class.java).addOrUpdate(accountTempInfo)
                }
                //全部存完了临时表后，再转存到本表
                val newTempAccountInfoList = SamDBManager.getInstance().dao(GetAccountInfoListTempResponse::class.java).queryAll()
                for (rowsDataEntity in newTempAccountInfoList) {
                    val accountTempInfo = GetAccountInfoListResponse(rowsDataEntity);
                    SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).addOrUpdate(accountTempInfo)
                }
                L.e("结束存newAccountInfoList.size()=" + newAccountInfoList.size)
            }
        }
    }

    fun setSyncCallback(callback: SyncAccountCallback) {
        this.callback = callback
    }

    interface SyncAccountCallback {
        fun complete()
    }
}