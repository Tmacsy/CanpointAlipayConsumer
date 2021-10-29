package com.sam.canpoint.ecard.ui.setting

import androidx.lifecycle.MutableLiveData
import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.model.IBaseModel

class VolumeSettingsViewModel : BaseViewModel<IBaseModel>() {
    override fun createModel(): IBaseModel? {
        return null
    }

    val maxVolume = MutableLiveData<Int>()
    val currentVolume = MutableLiveData<Int>()
}