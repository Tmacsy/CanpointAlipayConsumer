package com.sam.canpoint.ecard.ui.refund

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.RefundResult
import com.sam.canpoint.ecard.api.bean.RefundResultBean
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.Utils
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.http.SamApiManager
import com.sam.system.log.L
import com.sam.utils.device.DeviceUtils
import com.tyx.base.mvvm.exception.ExceptionHandle
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver

class RefundModel : AliPayBaseModel() {

    fun refund(orderId: String, result: (RefundResult) -> Unit) {
        val consumptionLocalRecord = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                .queryOne(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._orderId, orderId))
        SamApiManager.getInstance().getService(ICanPointApi::class.java).refund(orderId)
                .compose(applySchedulers(object : BaseRxObserver<RefundResultBean>(this) {
                    override fun onSuccess(it: RefundResultBean, message: String?) {
                        consumptionLocalRecord.businessChannel = CanPointSp.REFUND
                        L.d("退款成功:consumptionLocalRecord=" + consumptionLocalRecord.timeStamp)
                        SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java).update(consumptionLocalRecord)
                        val refundResult = RefundResult(it.refundName, it.refundPrice, it.createTime.substring(5), it.orderId, "退款成功", RefundResult.CODE_SUCCESS)
                        result.invoke(refundResult)
                    }

                    override fun onFail(it: Throwable?) {
                        val refundResult: RefundResult
                        if (it is ExceptionHandle.ResponseThrowable) {
                            Utils.postCatchException("退款失败的ResponseThrowable异常，userName=" + consumptionLocalRecord.userName
                                    + ",zfbUid=" + consumptionLocalRecord.zfbUid + ",设备SN=" + DeviceUtils.getAndroidID())
                            refundResult = RefundResult("", null, null, null, it.msg, it.code)
                            result.invoke(refundResult)
                        } else {
                            Utils.postCatchException("退款失败的非ResponseThrowable异常，userName=" + consumptionLocalRecord.userName
                                    + ",zfbUid=" + consumptionLocalRecord.zfbUid + ",设备SN=" + DeviceUtils.getAndroidID())
                            refundResult = RefundResult("", null, null, null, "退款失败", -1)
                            result.invoke(refundResult)
                        }
                    }
                }))
    }
}