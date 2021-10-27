package com.sam.canpoint.ecard.ui.pay

import android.text.TextUtils
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.sam.canpoint.ecard.api.IPayApi
import com.sam.canpoint.ecard.api.bean.AliPayResult
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListResponse
import com.sam.canpoint.ecard.api.bean.PayResultBean
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.api.request.FacePayEntity
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.PayDigestUtil
import com.sam.canpoint.ecard.utils.Utils
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.http.SamApiManager
import com.sam.utils.device.DeviceUtils
import com.sam.utils.network.NetworkUtils
import com.tyx.base.mvvm.exception.ExceptionHandle
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

class PayModel : BasePayModel() {

    private var onlineDisposable: Disposable? = null
    private var offlineDisposable: Disposable? = null

    fun pay(response: Smile2PayResponse, payPrice: String, result: (AliPayResult) -> Unit) {
        if (NetworkUtils.isNetAvailable()) onLinePay(response, payPrice, result)
        else offLinePay(response, payPrice, result)
    }

    private fun onLinePay(response: Smile2PayResponse, payPrice: String, result: (AliPayResult) -> Unit) {
        val entity = FacePayEntity()
        onlineDisposable = Observable.create<PayResultBean> {
            val map = getSignMap(response, payPrice, CanPointSp.ONLINE_MODE)
            SamApiManager.getInstance().getService(IPayApi::class.java).facePay(entity.initEntity(map))
                    .compose(applySchedulers(object : BaseRxObserver<PayResultBean>(this) {
                        override fun onSuccess(d: PayResultBean?, message: String?) {
                            it.onNext(d)
                            it.onComplete()
                        }

                        override fun onFail(e: Throwable?) {
                            it.onError(e)
                        }
                    }))
        }
                .doOnNext { data ->
                    //每次在线支付成功后，将该用户本地的欠费金额的值置为0.0
                    val queryUser = SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java)
                            .queryOne(WhereInfo.get().equal(GetAccountInfoListResponse._userCode, data?.userCode))
                    if (null != queryUser) {
                        queryUser.debtingAmount = BigDecimal(0.0)
                        SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).update(queryUser)
                    }
                    saveLocalRecord(entity, data?.userName, data?.arriveAmount, true, data?.businessChannel, data?.orderId)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val payResult = AliPayResult(it.userName, it.discountAmount, it.arriveAmount, it.orderAmount, it.mobile, it.createTime, "支付成功!", Smile2PayResponse.CODE_SUCCESS)
                    result(payResult)
                }, {
                    initPayError(response, payPrice, it, entity, result)
                })
    }

    private fun initPayError(response: Smile2PayResponse, payPrice: String, throwable: Throwable, entity: FacePayEntity, result: (AliPayResult) -> Unit) {
        val payResult: AliPayResult
        if (throwable is ExceptionHandle.ResponseThrowable) {
            if (throwable.code == 1006 || throwable.code == 502 || throwable.code == 404 || throwable.code == 1000) {
                Utils.postCatchException("支付超时开始走离线，faceToken=" +
                        response.faceToken + ",errorMsg=" + throwable.msg + ",errorCode=" + throwable.code + ",设备SN=" + DeviceUtils.getAndroidID())
                offLinePay(response, payPrice, result, entity.uuidFlag)
            } else {
                if (throwable.code == 1502) {
                    //每次在线支付出现欠款1502后，将该用户本地的欠费金额的值置为0.01,表示有欠款，限制脱机消费
                    val queryUser = SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java)
                            .queryOne(WhereInfo.get().equal(GetAccountInfoListResponse._zfbUid, entity.uid))
                    if (null != queryUser) {
                        queryUser.debtingAmount = BigDecimal(0.01)
                        SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java).update(queryUser)
                    }
                }
                Utils.postCatchException("支付走了ResponseThrowable异常，faceToken=" +
                        response.faceToken + ",errorMsg=" + throwable.msg + ",errorCode=" + throwable.code + ",设备SN=" + DeviceUtils.getAndroidID())
                payResult = AliPayResult("", null, null, null, null, 0, throwable.msg, throwable.code)
                result.invoke(payResult)
            }
        } else {
            val msg = if (TextUtils.isEmpty(throwable.message)) "支付失败" else throwable.message
            Utils.postCatchException("支付走了非ResponseThrowable的其他异常，faceToken=" +
                    response.faceToken + ",errorMsg=" + msg + ",设备SN=" + DeviceUtils.getAndroidID())
            payResult = AliPayResult("", null, null, null, null, 0, msg, -1)
            result.invoke(payResult)
        }
    }

    private fun offLinePay(response: Smile2PayResponse, payPrice: String, result: (AliPayResult) -> Unit, uuid: String? = null) {
        offlineDisposable = Observable.create<AliPayResult> {
            val accountInfo = SamDBManager.getInstance().dao(GetAccountInfoListResponse::class.java)
                    .queryOne(WhereInfo.get().equal(GetAccountInfoListResponse._zfbUid, response.alipayUid))
            //离线记录
            val offlineRecords = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                    .query(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._zfbUid, response.alipayUid)
                            .equal(ConsumptionLocalRecordsRequest._isSynchronized, false))
            var sumOfflineAmount = BigDecimal(0)
            if (offlineRecords.size > 0) {
                for (i in offlineRecords.indices) {
                    sumOfflineAmount += offlineRecords[i].arriveAmount
                }
            }
            val initOffLineResult = initOffLineResult(accountInfo, payPrice, sumOfflineAmount)
            it.onNext(initOffLineResult)
            it.onComplete()
        }
                .doOnNext { OffLineResult ->
                    if (OffLineResult.payResultCode == Smile2PayResponse.CODE_SUCCESS) {
                        val map = getSignMap(response, payPrice, CanPointSp.OFFLINE_MODE)
                        val entity = FacePayEntity()
                        saveLocalRecord(entity.initEntity(map, uuid), OffLineResult.payName, OffLineResult.arriveAmount, false, CanPointSp.OFFLINE)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result.invoke(it)
                }, {
                    val aliPayResult = initResultPageBean(null, null, null, GetAccountInfoListResponse(), "用户数据异常!", -6)
                    result.invoke(aliPayResult)
                })
    }

    override fun cancel() {
        super.cancel()
        onlineDisposable?.dispose()
        offlineDisposable?.dispose()
    }
}