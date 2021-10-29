package com.sam.canpoint.ecard.ui.refund

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.api.bean.RefundResult
import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.livedata.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RefundViewModel : BaseViewModel<RefundModel>() {
    override fun createModel(): RefundModel = RefundModel()

    val netWorkState = MutableLiveData<Int>() //网络状态
    val countDownTime = MutableLiveData<String>() //倒计时
    val close = SingleLiveEvent<String>() //关闭
    val refundResult = MutableLiveData<RefundResult>()
    val currentDataTime = MutableLiveData<String>()
    private var mCountDownDisposable: Disposable? = null

    init {
        currentDataTime.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    fun refund(orderId: String) {
        model?.refund(orderId, result = {
            refundResult.value = it
            startCountDown(5)
        })
    }

    private fun startCountDown(count: Long) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(count + 1)
                .map { aLong: Long -> count - aLong }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Long?> {
                    override fun onSubscribe(d: @NonNull Disposable?) {
                        mCountDownDisposable = d
                    }

                    override fun onNext(aLong: @NonNull Long?) {
                        countDownTime.value = "返回 ${aLong}S"
                    }

                    override fun onError(e: @NonNull Throwable?) {}
                    override fun onComplete() {
                        close.call()
                    }
                })
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        mCountDownDisposable?.dispose()
    }
}