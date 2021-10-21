package com.tyx.base.mvvm

import androidx.lifecycle.*
import com.tyx.base.mvvm.model.IBaseModel
import com.tyx.base.mvvm.livedata.SingleLiveEvent
import org.jetbrains.annotations.NotNull

abstract class BaseViewModel<M : IBaseModel?> : ViewModel(), LifecycleObserver {

    val model: M? by lazy { createModel() }

    abstract fun createModel(): M?

    val viewChange: ViewChange by lazy { ViewChange() }

    inner class ViewChange {
        val showLoadingDialog by lazy { SingleLiveEvent<String>() }
        val dismissDialog by lazy { SingleLiveEvent<String>() }
        val showToast by lazy { SingleLiveEvent<String>() }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    open fun onCreate(@NotNull owner: LifecycleOwner) {

    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    open fun onResume(@NotNull owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    open fun onPause(@NotNull owner: LifecycleOwner) {
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(@NotNull owner: LifecycleOwner) {
        model?.cancel()
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_ANY)
    open fun onLifecycleChanged(@NotNull owner: LifecycleOwner, @NotNull event: Lifecycle.Event) {
    }

    override fun onCleared() {
        super.onCleared()
    }
}