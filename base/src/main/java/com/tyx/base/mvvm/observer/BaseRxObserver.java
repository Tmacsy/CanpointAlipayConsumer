package com.tyx.base.mvvm.observer;

import androidx.annotation.Nullable;

import com.tyx.base.mvvm.model.IBaseModel;

import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseRxObserver<D> extends BaseObserver<D> {

    private IBaseModel model;

    public BaseRxObserver(IBaseModel model) {
        this.model = model;
    }

    @Override
    public void onSubscribe(@Nullable Disposable d) {
        super.onSubscribe(d);
        if (model != null) {
            model.addDisposable(d);
        }
    }
}
