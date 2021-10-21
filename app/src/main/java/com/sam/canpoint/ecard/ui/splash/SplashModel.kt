package com.sam.canpoint.ecard.ui.splash

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.AddDeviceBindResponse
import com.sam.canpoint.ecard.api.bean.GetConsumeRuleResponse
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.api.bean.SplashZipData
import com.sam.canpoint.ecard.api.request.AddDeviceBindRequest
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.sam.http.SamApiManager
import com.sam.utils.device.DeviceUtils
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SplashModel : AliPayBaseModel() {

    fun getData(success: (SplashZipData?) -> Unit, error: (Throwable) -> Unit, complete: () -> Unit = {}, disposable: (Disposable?) -> Unit = {}) {
        Observable.zip(getConsumerRule(), addDeviceBind(), merchantInfo(), { getConsumeRuleResponses, addDeviceBindResponse, merchantInfoBean ->
            SplashZipData(getConsumeRuleResponses, addDeviceBindResponse, merchantInfoBean)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<SplashZipData> {
                    override fun onSubscribe(d: Disposable?) {
                        disposable.invoke(d)
                    }

                    override fun onNext(t: SplashZipData?) {
                        success.invoke(t)
                    }

                    override fun onError(e: Throwable) {
                        error.invoke(e)
                    }

                    override fun onComplete() {
                        complete.invoke()
                    }
                })
    }

    //获取学校消费规则
    private fun getConsumerRule(): Observable<ArrayList<GetConsumeRuleResponse>> {
        return object : Observable<ArrayList<GetConsumeRuleResponse>>() {
            override fun subscribeActual(observer: Observer<in ArrayList<GetConsumeRuleResponse>>) {
                SamApiManager.getInstance().getService(ICanPointApi::class.java).getConsumerRule(CanPointSp.schoolCode)
                        .compose(applySchedulers(object : BaseRxObserver<ArrayList<GetConsumeRuleResponse>>(this@SplashModel) {
                            override fun onSuccess(d: ArrayList<GetConsumeRuleResponse>?, message: String?) {
                                observer.onNext(if (d.isNullOrEmpty()) ArrayList() else d)
                            }

                            override fun onFail(e: Throwable?) {
                                observer.onError(e)
                            }
                        }))
            }
        }
    }

    //消费机注册
    private fun addDeviceBind(): Observable<AddDeviceBindResponse> {
        val request = AddDeviceBindRequest()
        return object : Observable<AddDeviceBindResponse>() {
            override fun subscribeActual(observer: Observer<in AddDeviceBindResponse>) {
                SamApiManager.getInstance().getService(ICanPointApi::class.java).addDeviceBind(request.apply {
                    schoolCode = CanPointSp.schoolCode
                    sn = DeviceUtils.getAndroidID()
                    operateType = "2" //查询TOKEN
                }).compose(applySchedulers(object : BaseRxObserver<AddDeviceBindResponse>(this@SplashModel) {
                    override fun onSuccess(d: AddDeviceBindResponse?, message: String?) {
                        observer.onNext(d)
                    }

                    override fun onFail(e: Throwable?) {
                        observer.onError(e)
                    }
                }))
            }
        }
    }

    //支付宝初始化相关数据
    private fun merchantInfo(): Observable<MerchantInfoBean> {
        return object : Observable<MerchantInfoBean>() {
            override fun subscribeActual(observer: Observer<in MerchantInfoBean>) {
                SamApiManager.getInstance().getService(ICanPointApi::class.java).merchantInfo()
                        .compose(applySchedulers(object : BaseRxObserver<MerchantInfoBean>(this@SplashModel) {
                            override fun onSuccess(d: MerchantInfoBean?, message: String?) {
                                observer.onNext(d)
                            }

                            override fun onFail(e: Throwable?) {
                                observer.onError(e)
                            }
                        }))
            }
        }
    }
}