package com.sam.canpoint.ecard.ui.init

import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.tyx.base.mvvm.BaseViewModel

class BindAddressViewModel : BaseViewModel<BindAddressModel>() {
    override fun createModel(): BindAddressModel = BindAddressModel()
    val deviceName = MutableLiveData<String>()
    val storeName = MutableLiveData<String>()
    val storeList = MutableLiveData<ArrayList<GetStoreInfoResponse>>()

    fun getStoreInfo() {
        model?.getStore(success = {
            if (it.isNullOrEmpty()) return@getStore
            storeList.value = it
        }, error = {
            viewChange.showToast.value = it?.message
        })
    }
}