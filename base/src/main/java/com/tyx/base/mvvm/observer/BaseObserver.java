package com.tyx.base.mvvm.observer;
import com.sam.system.log.L;
import com.tyx.base.bean.BaseResponse;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public abstract class BaseObserver<D> implements Observer<BaseResponse<D>> {

    protected Disposable disposable;

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        this.disposable = d;
    }

    @Override
    public void onNext(@NonNull BaseResponse<D> dBaseResponse) {
        if (disposable != null) {
            if (!disposable.isDisposed()) {
                getAllData(dBaseResponse);
                onSuccess(dBaseResponse.getData(), dBaseResponse.getMessage());
                onComplete();
            } else {
                L.d("当前网络请求已取消!");
            }
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onFail(e);
    }

    @Override
    public void onComplete() {

    }


    public abstract void onSuccess(D d, String message);

    public abstract void onFail(Throwable e);

    protected void getAllData(BaseResponse<D> data) {

    }
}
