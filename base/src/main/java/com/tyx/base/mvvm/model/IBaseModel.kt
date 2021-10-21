package com.tyx.base.mvvm.model

import io.reactivex.rxjava3.disposables.Disposable

interface IBaseModel {

    fun addDisposable(d: Disposable)

    fun cancel()
}