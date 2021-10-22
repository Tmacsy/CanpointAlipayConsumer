package com.sam.canpoint.ecard.ui.home

import androidx.lifecycle.MutableLiveData
import com.alipay.zoloz.smile2pay.InstallCallback
import com.alipay.zoloz.smile2pay.Zoloz
import com.alipay.zoloz.smile2pay.ZolozConstants
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.application.CanPointECardApplication
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.sam.system.log.L
import com.sam.utils.network.NetworkUtils
import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.exception.ExceptionHandle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable

class HomeViewModel : BaseViewModel<HomeModel>() {
    override fun createModel(): HomeModel = HomeModel()
    val netWorkState = MutableLiveData<Int>() //网络状态
    private var zoloz: Zoloz? = null
    private var merchantInfo: Map<String, String>? = null

    init {
        zoloz = Zoloz.getInstance(CanPointECardApplication.the())
    }

    fun initSmile() {
        Observable.create<MerchantInfoBean> { emitter ->
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
                .doOnNext {
                    model?.releaseIOT()
                }
                .flatMap {
                    initZoloz(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                }, {

                })
    }

    private fun initIOT(info: MerchantInfoBean): Observable<MerchantInfoBean> {
        return Observable.create { emitter ->
            if (CanPointSp.iotStatus) {
                L.d("IOT已初始化")
                emitter.onNext(info)
                emitter.onComplete()
            } else {
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
}