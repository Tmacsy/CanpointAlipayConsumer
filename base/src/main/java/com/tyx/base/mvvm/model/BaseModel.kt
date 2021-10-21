package com.tyx.base.mvvm.model

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

open class BaseModel : IBaseModel {

    override fun addDisposable(d: Disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(d)
    }

    private var compositeDisposable: CompositeDisposable? = null

    override fun cancel() {
        compositeDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

}