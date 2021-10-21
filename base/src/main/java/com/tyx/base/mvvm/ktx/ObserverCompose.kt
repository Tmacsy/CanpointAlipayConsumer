package com.tyx.base.mvvm.ktx
import com.tyx.base.bean.BaseResponse
import com.tyx.base.mvvm.exception.ExceptionHandle
import com.tyx.base.mvvm.exception.HttpErrorHandler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers


fun <T> applySchedulers(observer: Observer<T>?): ObservableTransformer<T, T>? {
    return ObservableTransformer { upstream ->
        val observable = upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(getAppErrorHandler())
                .onErrorResumeNext(HttpErrorHandler()) as Observable<T>
        observable.subscribe(observer)
        observable
    }
}

fun <T> getAppErrorHandler(): Function<T, T> {
    return Function { response -> //response中code码不为200 出现错误
        if (response is BaseResponse<*> && response.code != 200) {
            val exception: ExceptionHandle.ServerException = ExceptionHandle.ServerException()
            exception.code = response.code
            exception.msg = if (response.message != null) response.message else ""
            throw exception
        }
        response
    }
}


