package com.sam.canpoint.ecard.ui.home

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.LocalRecordBean
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.api.bean.OrderDetailResponse
import com.sam.canpoint.ecard.api.bean.StatisticsByDayResponse
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.forMatTime
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.http.SamApiManager
import com.tyx.base.mvvm.exception.ExceptionHandle
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeModel : AliPayBaseModel() {

    private var mSearchHistoryDisposable: Disposable? = null

    fun merchantInfo(isNet: Boolean = true, success: (MerchantInfoBean) -> Unit, error: (Throwable?) -> Unit) {
        if (isNet) {
            SamApiManager.getInstance().getService(ICanPointApi::class.java).merchantInfo()
                    .compose(applySchedulers(object : BaseRxObserver<MerchantInfoBean>(this) {
                        override fun onSuccess(d: MerchantInfoBean, message: String?) {
                            success.invoke(d)
                        }

                        override fun onFail(e: Throwable?) {
                            //失败
                            merchantInfo(false, success, error)
                        }
                    }))
        } else {
            val queryOne = SamDBManager.getInstance().dao(MerchantInfoBean::class.java).queryOne(WhereInfo.get())
            if (queryOne != null) {
                success.invoke(queryOne)
            } else {
                error.invoke(ExceptionHandle.ResponseThrowable("获取商户信息失败!", -10))
            }
        }
    }

    /**
     * 刷脸参数配置
     * faceModel = 1 自由模式 2 定额模式
     */
    fun mockConfigInfo(faceModel: Int, price: String): Map<String, Any> {
        val map = HashMap<String, Any>()
        return map.apply {
            val configInfo = JSONObject() //设置参数配置-start
            configInfo[ZolozConfig.KEY_MODE_FACE_MODE] = ZolozConfig.FaceMode.FACEPAY // 必填项，模式
            configInfo[ZolozConfig.KEY_ALGORITHM_MAX_DETECT_DISTANCE] = 750 // 必填项，取值：0～1000mm，建议值：750mm
            put(ZolozConfig.KEY_ZOLOZ_CONFIG, configInfo.toJSONString())
            //设置参数配置-end
            // -------------------------------------------------------------------------------------
            //UI参数配置-start
            val uiConfig = JSONObject()
            when (faceModel) {
                //自由模式
                1 -> {
                    uiConfig[ZolozConfig.KEY_UI_SHOW_PAYMENT_CODE] = false //是否显示"付款码支付"按钮，true:显示，默认false
                    uiConfig[ZolozConfig.KEY_UI_PAY_AMOUNT] = price.toFloat() //自由模式，支付金额，可选是否显示，非必须
                }
                //定额模式
                2 -> {
                    uiConfig[ZolozConfig.KEY_UI_SHOW_PAYMENT_CODE] = false //是否显示"付款码支付"按钮，true:显示，默认false
                    uiConfig[ZolozConfig.KEY_UI_PAY_AMOUNT] = price.toFloat() //定额模式，支付金额，必填项
                    uiConfig[ZolozConfig.KEY_UI_ENABLE_TIME_OUT] = false //定额模式，必填项，采集页面否启用超时机制(boolean)，false：禁用超时机制。默认：true
                }
            }
            put(ZolozConfig.KEY_UI_CONFIG, uiConfig.toJSONString())
            //UI参数配置-end
            //--------------------------------------------------------------------------------------
            //主,副评显示 默认主频  SMILE_MODE_EXT_DISPLAY副屏
            put(ZolozConfig.KEY_SMILE_MODE, ZolozConfig.SmileMode.SMILE_MODE_DEFAULT_DISPLAY)
        }
    }

    private var flag = true

    fun pwdVerify(pwd: String, success: (String) -> Unit, error: (Throwable?) -> Unit) {
        if (!flag) return
        flag = false
        val map = HashMap<String, String>()
        SamApiManager.getInstance().getService(ICanPointApi::class.java).verifyPassword(map.apply {
            put("password", pwd)
            put("schoolCode", CanPointSp.schoolCode)
        }).compose(applySchedulers(object : BaseRxObserver<String>(this) {
            override fun onSuccess(d: String?, message: String?) {
                success.invoke("success")
            }

            override fun onFail(e: Throwable?) {
                if (!TextUtils.isEmpty(map["password"]) && map["password"].equals(CanPointSp.password)) {
                    success.invoke("success")
                } else {
                    error.invoke(e)
                }
            }

            override fun onComplete() {
                super.onComplete()
                flag = true
            }
        }))
    }

    /**
     * 查询某一页的离线记录
     * @param isDown = true  选中下标0  false 选中下表末尾
     */
    fun searchLocalHistory(currentPage: Int, isDown: Boolean, result: (ArrayList<LocalRecordBean>) -> Unit) {
        mSearchHistoryDisposable = Observable.create<ArrayList<ConsumptionLocalRecordsRequest>> {
            val whereInfo = WhereInfo.get()
            whereInfo.limit = 6
            whereInfo.currentPage = currentPage
            val consumptionLocalRecordsRequests = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                    .queryLimit(whereInfo.order(ConsumptionLocalRecordsRequest._timeStamp, false))
            it.onNext(consumptionLocalRecordsRequests as ArrayList<ConsumptionLocalRecordsRequest>?)
            it.onComplete()
        }
                .takeWhile {
                    !it.isNullOrEmpty()
                }
                .flatMap {
                    Observable.fromIterable(it)
                }
                .map {
                    val bean = LocalRecordBean()
                    val time = it.timeStamp.toLong()
                    bean.businessChannel = it.businessChannel
                    bean.name = it.userName
                    bean.time = (time * 1000).forMatTime()
                    bean.actualAmount = it.arriveAmount.toString()
                    // 后端给的接口需增加是否同步的字段
                    bean.isSynchronized = it.isSynchronized
                    bean.isOffline = CanPointSp.OFFLINE_MODE == it.isOnline
                    bean.orderId = it.orderId
                    bean
                }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    val beans = list as ArrayList<LocalRecordBean>
                    beans[if (isDown) 0 else beans.size - 1].isSelected = true
                    result.invoke(beans)
                }
    }

    fun startQueryOrder(orderId: String, success: (OrderDetailResponse?) -> Unit, error: (Throwable?) -> Unit) {
        SamApiManager.getInstance().getService(ICanPointApi::class.java).queryOrderDetail(orderId)
                .compose(applySchedulers(object : BaseRxObserver<OrderDetailResponse>(this) {
                    override fun onSuccess(d: OrderDetailResponse?, message: String?) {
                        success.invoke(d)
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }
                }))
    }

    fun changeDeviceBind(type: String, success: () -> Unit, error: (Throwable?) -> Unit) {
        if (CanPointSp.patternType == type) return
        val map = HashMap<String, String>()
        map["consumerModel"] = type
        map["deviceName"] = CanPointSp.devicesName
        map["schoolCode"] = CanPointSp.schoolCode
        map["storeId"] = CanPointSp.storeId
        SamApiManager.getInstance().getService(ICanPointApi::class.java).changeDeviceBind(map)
                .compose(applySchedulers(object : BaseRxObserver<String>(this) {
                    override fun onSuccess(d: String?, message: String?) {
                        success.invoke()
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }
                }))
    }

    fun getDeviceStatisticsByDay(success: (StatisticsByDayResponse) -> Unit, error: (Throwable?) -> Unit) {
        SamApiManager.getInstance().getService(ICanPointApi::class.java).getDeviceStatisticsByDay()
                .compose(applySchedulers(object : BaseRxObserver<StatisticsByDayResponse>(this) {
                    override fun onSuccess(d: StatisticsByDayResponse, message: String?) {
                        success.invoke(d)
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }
                }))
    }

    fun changePassWord(newPwd: String, success: () -> Unit, error: (Throwable?) -> Unit) {
        SamApiManager.getInstance().getService(ICanPointApi::class.java).updatePassword(newPwd)
                .compose(applySchedulers(object : BaseRxObserver<String>(this) {
                    override fun onSuccess(d: String?, message: String?) {
                        success.invoke()
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }
                }))
    }

    override fun cancel() {
        super.cancel()
        mSearchHistoryDisposable?.dispose()
    }
}