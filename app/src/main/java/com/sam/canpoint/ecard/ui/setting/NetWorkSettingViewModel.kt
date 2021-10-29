package com.sam.canpoint.ecard.ui.setting

import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.livedata.SingleLiveEvent
import com.tyx.base.mvvm.model.IBaseModel

class NetWorkSettingViewModel : BaseViewModel<IBaseModel>() {
    override fun createModel(): IBaseModel? {
        return null
    }

    val startWifiSettings = SingleLiveEvent<String>() //跳转系统WiFi设置页面
}