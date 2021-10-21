package com.sam.canpoint.ecard.ui.init

import com.tyx.base.mvvm.BaseViewModel

class SyncDataViewModel : BaseViewModel<SyncDataModel>() {
    override fun createModel(): SyncDataModel = SyncDataModel()
}