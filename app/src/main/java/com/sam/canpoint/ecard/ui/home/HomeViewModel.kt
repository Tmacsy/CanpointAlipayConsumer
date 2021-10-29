package com.sam.canpoint.ecard.ui.home

import android.text.TextUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.alipay.zoloz.smile2pay.InstallCallback
import com.alipay.zoloz.smile2pay.Zoloz
import com.alipay.zoloz.smile2pay.ZolozConstants
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.alipay.zoloz.smile2pay.verify.VerifyCallback
import com.sam.canpoint.ecard.api.bean.*
import com.sam.canpoint.ecard.application.CanPointECardApplication
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.checkPrice
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.Utils
import com.sam.system.log.L
import com.sam.utils.device.DeviceUtils
import com.sam.utils.network.NetworkUtils
import com.tencent.bugly.crashreport.CrashReport
import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.exception.ExceptionHandle
import com.tyx.base.mvvm.livedata.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

class HomeViewModel : BaseViewModel<HomeModel>() {
    override fun createModel(): HomeModel = HomeModel()
    val netWorkState = MutableLiveData<Int>() //网络状态
    private var zoloz: Zoloz? = null  //刷脸
    private var merchantInfo: Map<String, String>? = null
    private var isInitSuccess = false
    private var initSmileDisposable: Disposable? = null
    private var startSmileDisposable: Disposable? = null
    val inputPrice = MutableLiveData<String>()   //输入金额
    val smileResultLiveData = MutableLiveData<SmileResult>() //刷脸结果
    val showPresentation = MutableLiveData<String>() //副屏展示
    val presentationMode = MutableLiveData<Int>() //输入金额副屏显示模式
    val addPrice = MutableLiveData<String>() //加号操作显示
    var initialPrice = "0" //加号运算前初始金额
    val showCancelView = MutableLiveData<Int>() //交易中 弹框提示  0隐藏  1取消 2提示交易  3限制交易
    val passWord = MutableLiveData<String>()  //输入密码副屏输入
    val passWordError = SingleLiveEvent<String>() //输入密码失败
    var verifyType = CanPointSp.VERIFY_FUNCTION
    val updateQuotaMoneySucceed = SingleLiveEvent<String>() //更新新的定额成功
    val startConfirmOrder = MutableLiveData<OrderDetailResponse?>() //跳转
    val isUpdateLocalRecord = MutableLiveData<Boolean>() //是否更新本地记录
    val changePasswordResult = MutableLiveData<Boolean>() //修改密码结果

    init {
        zoloz = Zoloz.getInstance(CanPointECardApplication.the())
    }

    fun initSmile(success: (Smile2PayResponse) -> Unit = {}, error: (Throwable) -> Unit = {}) {
        viewChange.showLoadingDialog.call()
        initSmileDisposable = Observable.create<MerchantInfoBean> { emitter ->
            model?.merchantInfo(NetworkUtils.isNetAvailable(), success = {
                emitter.onNext(it)
                emitter.onComplete()
            }, error = {
                emitter.onError(it)
            })
        }
                .flatMap { info ->
                    initIOT(info)
                }
                .map { info ->
                    val map = HashMap<String, String>()
                    map.apply {
                        put(ZolozConstants.KEY_MERCHANT_INFO_ISV_PID, info.isvPid)
                        put(ZolozConstants.KEY_MERCHANT_INFO_ISV_NAME, info.isvName)
                        put(ZolozConstants.KEY_MERCHANT_INFO_MERCHANT_ID, info.merchantId)
                        put(ZolozConstants.KEY_MERCHANT_INFO_MERCHANT_NAME, info.merchantName)
                        put(ZolozConstants.KEY_MERCHANT_INFO_MERCHANT_PAY_PID, info.merchantPayPid)
                        put(ZolozConstants.KEY_MERCHANT_INFO_DEVICE_NUM, info.deviceNum)
                        put(ZolozConstants.KEY_GROUP_ID, info.groupID)
                    }
                }
                .flatMap {
                    initZoloz(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    isInitSuccess = true
                    viewChange.dismissDialog.call()
                    success.invoke(it)
                }, {
                    isInitSuccess = false
                    viewChange.dismissDialog.call()
                    error.invoke(it)
                })
    }

    private fun initIOT(info: MerchantInfoBean): Observable<MerchantInfoBean> {
        return Observable.create { emitter ->
            model?.initIOT(info, result = {
                if (it) {
                    emitter.onNext(info)
                    emitter.onComplete()
                } else {
                    emitter.onError(ExceptionHandle.ResponseThrowable("初始化IOT失败!", -11))
                }
            })
        }
    }

    private fun initZoloz(map: HashMap<String, String>): Observable<Smile2PayResponse> {
        merchantInfo = map
        return Observable.create { emitter ->
            zoloz?.install(map as Map<String, Any>?)
            zoloz?.register(null, object : InstallCallback() {
                override fun onResponse(p0: Smile2PayResponse?) {
                    p0?.let {
                        if (it.code == Smile2PayResponse.CODE_SUCCESS) {
                            emitter.onNext(it)
                            emitter.onComplete()
                        } else {
                            emitter.onError(ExceptionHandle.ResponseThrowable(it.subMsg, -13))
                        }
                    } ?: emitter.onError(ExceptionHandle.ResponseThrowable("初始化刷脸失败!", -11))
                }

                override fun onEvent(code: Int, message: String?) {
                    super.onEvent(code, message)
                    val msg = String.format("code:%d, msg:%s", code, message)
                    emitter.onError(ExceptionHandle.ResponseThrowable(msg, -14))
                }
            })
            smileOnConnect()
        }
    }

    /**
     * 监听连接状态
     */
    private fun smileOnConnect() {
        zoloz?.setConnectCallback { connected, componentName ->
            if (!connected) {
                L.e("刷脸重连:${componentName}")
                zoloz?.install(merchantInfo)
                zoloz?.register(null, object : InstallCallback() {
                    override fun onResponse(p0: Smile2PayResponse?) {}
                })
            }
        }
    }

    fun startSmile() {
        if (!isInitSuccess) {
            initSmile(success = { startSmile() })
            return
        }
        val mode = presentationMode.value
        val show = showPresentation.value
        if (show != PresentationFactory.INPUT_AMOUNT) return
        if (mode != InputAmountPresentation.FREE_MODE_1 && mode != InputAmountPresentation.QUOTA_MODE_1) return
        val price = inputPrice.value ?: ""
        if (!price.checkPrice()) return
        if (mode == InputAmountPresentation.FREE_MODE_1) presentationMode.value = InputAmountPresentation.FREE_MODE_2
        else if (mode == InputAmountPresentation.QUOTA_MODE_1) presentationMode.value = InputAmountPresentation.QUOTA_MODE_2
        startSmileDisposable = Observable.create<Smile2PayResponse> {
            zoloz?.verify(model?.mockConfigInfo(CanPointSp.patternType.toInt(), price), object : VerifyCallback() {
                override fun onResponse(p0: Smile2PayResponse?) {
                    p0?.let { response ->
                        if (response.code == Smile2PayResponse.CODE_SUCCESS) {
                            if (response.faceToken.startsWith("FACE")) {
                                Utils.postCatchException("刷脸付没有开通,faceToken=${response.faceToken}," +
                                        "设备SN=${DeviceUtils.getAndroidID()},aliPayUid= +${response.alipayUid}")
                                it.onError(ExceptionHandle.ResponseThrowable("刷脸付没有开通!", -15))
                            } else {
                                it.onNext(response)
                                it.onComplete()
                            }
                        } else {
                            if (response.code == 1003) {
                                //退出刷脸服务
                                when (response.subCode) {
                                    "Z1008" -> {
                                        //取消键退出
                                        it.onError(ExceptionHandle.ResponseThrowable(response.subMsg, -4))
                                    }
                                    "Z6002" -> {
                                        //主屏 X 退出
                                        it.onError(ExceptionHandle.ResponseThrowable(response.subMsg, -5))
                                    }
                                    else -> {
                                        it.onError(ExceptionHandle.ResponseThrowable(response.subMsg, response.code))
                                    }
                                }
                            } else {
                                it.onError(ExceptionHandle.ResponseThrowable(response.subMsg, response.code))
                            }
                        }
                    } ?: it.onError(ExceptionHandle.ResponseThrowable("刷脸服务异常!", -14))
                }
            })
        }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val smileResult = SmileResult(it, Smile2PayResponse.CODE_SUCCESS, price)
                    smileResultLiveData.value = smileResult
                }, {
                    if (it is ExceptionHandle.ResponseThrowable) {
                        val smileResult = SmileResult(null, it.code, price)
                        smileResultLiveData.value = smileResult
                        viewChange.showToast.value = it.message
                    }
                })
    }

    /**
     * 多个副屏需要复用
     */
    fun initInputPrice(num: String) {
        try {
            var price = inputPrice.value ?: ""
            //输入金额计算
            val isPoint = num == "."
            if (isPoint && price.endsWith(".")) return
            if (num == "0" && price.endsWith("0") && price.startsWith("0")) return
            price += num
            if (price == ".") price = "0."
            if (price.startsWith("0") && !price.contains(".") && price.length > 1)
                price = price[1].toString()
            if (price.contains(".") && !price.endsWith(".")) {
                val split = price.split("\\.".toRegex()).toTypedArray()
                if (split[0].length > 3 || split[1].length > 2) return
            } else {
                if (price.toDouble() > 999 && !isPoint) return
            }
            inputPrice.value = price
            initialPrice = if (TextUtils.isEmpty(price)) "0" else price
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pwdVerify(pwd: String, success: (String) -> Unit, error: (Throwable?) -> Unit) {
        model?.pwdVerify(pwd, success, error)
    }

    fun searchLocalHistory(pageNum: Int, isDown: Boolean, result: (ArrayList<LocalRecordBean>) -> Unit) {
        model?.searchLocalHistory(pageNum, isDown, result)
    }

    fun startQueryOrder(orderId: String, success: (OrderDetailResponse?) -> Unit, error: (Throwable?) -> Unit) {
        model?.startQueryOrder(orderId, success, error)
    }

    fun changeDeviceBind(type: String, success: () -> Unit, error: (Throwable?) -> Unit) {
        model?.changeDeviceBind(type, success, error)
    }

    fun getDeviceStatisticsByDay(success: (StatisticsByDayResponse) -> Unit, error: (Throwable?) -> Unit) {
        model?.getDeviceStatisticsByDay(success, error)
    }

    fun changePassword(newPwd: String, success: () -> Unit, error: (Throwable?) -> Unit) {
        model?.changePassWord(newPwd, success, error)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        initSmileDisposable?.dispose()
        startSmileDisposable?.dispose()
    }
}