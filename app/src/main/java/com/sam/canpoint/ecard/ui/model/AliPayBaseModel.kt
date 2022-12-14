package com.sam.canpoint.ecard.ui.model

import android.text.TextUtils
import com.alipay.iot.sdk.APIManager
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.application.CanPointECardApplication
import com.sam.canpoint.ecard.manager.IOTManager
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.Utils
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.system.log.L
import com.sam.utils.device.DeviceUtils
import com.tyx.base.mvvm.model.BaseModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception

open class AliPayBaseModel : BaseModel() {

    /**
     * 清除本地过期(超过七天)的已同步消费记录
     */
    open fun clearOverdueLocalRecord(success: (Int) -> Unit = {}, error: (Throwable) -> Unit = {}) {
        Observable.create<Int> {
            val delete = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                    .delete(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._isSynchronized, true)
                            .lt(ConsumptionLocalRecordsRequest._timeStamp, System.currentTimeMillis() / 1000L - (1000 * 60 * 60 * 24 * 7)))
            it.onNext(delete)
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    L.d("已清除7天前消费成功的本地记录${it}条")
                    success.invoke(it)
                }, {
                    error.invoke(it)
                })
    }


    /**
     * 上报bugLy 未上报的离线消费记录
     */
    open fun reportOfflineRecords() {
        Observable.create<Long> {
            val countOf = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                    .countOf(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._isSynchronized, false))
            it.onNext(countOf)
            it.onComplete()
        }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    L.v("当前设备的累计未上报${it}条离线消费记录")
                    if (it > 0) {
                        Utils.postCatchException("当前设备的累计未上报离线记录数量为${it},设备SN=${DeviceUtils.getAndroidID()}")
                    }
                }
    }

    open fun initIOT(data: MerchantInfoBean? = null, result: (Boolean) -> Unit = {}) {
        try {
            if (CanPointSp.iotStatus) {
                result.invoke(true)
                return
            }
            var infoBean = data
            if (infoBean == null) {
                val queryOne = SamDBManager.getInstance().dao(MerchantInfoBean::class.java).queryOne(WhereInfo.get())
                if (queryOne != null) infoBean = queryOne
            }
            if (infoBean != null) {
                APIManager.getInstance().deinitialize()
                APIManager.getInstance().initialize(CanPointECardApplication.the(), infoBean.isvPid) {
                    val value = it && !TextUtils.isEmpty(APIManager.getInstance().deviceAPI.deviceId)
                    if (value) {
                        L.d("IOT初始化成功...")
                    } else {
                        L.e("IOT初始化失败!")
                        IOTManager.get().voice("e9")
                        Utils.postCatchException("iot初始化失败的异常,设备SN=" + DeviceUtils.getAndroidID())
                    }
                    SamDBManager.getInstance().dao(MerchantInfoBean::class.java).addOrUpdate(infoBean)
                    CanPointSp.iotStatus = value
                    result.invoke(value)
                }
            } else {
                L.d("商户数据为空!")
                CanPointSp.iotStatus = false
                result.invoke(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CanPointSp.iotStatus = false
            result.invoke(false)
        }
    }
}