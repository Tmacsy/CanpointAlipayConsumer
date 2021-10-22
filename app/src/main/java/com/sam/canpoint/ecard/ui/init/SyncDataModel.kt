package com.sam.canpoint.ecard.ui.init

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.AutoInitDeviceZipData
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListResponse
import com.sam.canpoint.ecard.api.bean.InitDeviceResponse
import com.sam.canpoint.ecard.api.bean.SplashZipData
import com.sam.canpoint.ecard.ui.splash.SplashModel
import com.sam.http.SamApiManager
import com.sam.utils.device.DeviceUtils
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SyncDataModel : SplashModel() {

    fun autoInitDevice(success: (AutoInitDeviceZipData?) -> Unit, error: (Throwable?) -> Unit = {}, complete: () -> Unit = {}, disposable: (Disposable?) -> Unit = {}) {
        Observable.zip(initDevice(), merchantInfo(), { deviceResponse, merchantInfo -> AutoInitDeviceZipData(deviceResponse, merchantInfo) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<AutoInitDeviceZipData> {
                    override fun onSubscribe(d: Disposable?) {
                        disposable.invoke(d)
                    }

                    override fun onNext(t: AutoInitDeviceZipData?) {
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

    private fun initDevice(): Observable<InitDeviceResponse> {
        return object : Observable<InitDeviceResponse>() {
            override fun subscribeActual(observer: Observer<in InitDeviceResponse>) {
                SamApiManager.getInstance().getService(ICanPointApi::class.java).initDevice(DeviceUtils.getAndroidID())
                        .compose(applySchedulers(object : BaseRxObserver<InitDeviceResponse>(this@SyncDataModel) {
                            override fun onSuccess(d: InitDeviceResponse?, message: String?) {
                                observer.onNext(d)
                                observer.onComplete()
                            }

                            override fun onFail(e: Throwable?) {
                                observer.onError(e)
                            }
                        }))
            }
        }
    }
}