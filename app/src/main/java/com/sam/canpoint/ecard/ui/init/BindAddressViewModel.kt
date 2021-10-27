package com.sam.canpoint.ecard.ui.init

import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.api.bean.AddDeviceBindResponse
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.api.request.AddDeviceBindRequest
import com.sam.canpoint.ecard.utils.Utils
import com.sam.canpoint.ecard.utils.CanPointSp
import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.livedata.SingleLiveEvent

class BindAddressViewModel : BaseViewModel<BindAddressModel>() {
    override fun createModel(): BindAddressModel = BindAddressModel()
    val deviceName = MutableLiveData<String>()
    val storeName = MutableLiveData<String>()
    val storeListLiveData = MutableLiveData<ArrayList<GetStoreInfoResponse>>()
    val startClass = SingleLiveEvent<String>()

    fun getStoreInfo() {
        model?.getStore(success = {
            if (it.isNullOrEmpty()) return@getStore
            storeListLiveData.value = it
        }, error = {
            viewChange.showToast.value = it?.message
        })
    }

    fun addDeviceBind(request: AddDeviceBindRequest) {
        val value = deviceName.value
        if (value.isNullOrEmpty()) {
            viewChange.showToast.value = "消费机名称必填!"
            return
        }
        if (request.storeId.isNullOrEmpty()) {
            viewChange.showToast.value = "门店不能为空!"
            return
        }
        viewChange.showLoadingDialog.call()
        model?.addDeviceBind(request, success = {
            initData(it)
        }, error = {
            viewChange.showToast.value = it?.message
        }, complete = {
            viewChange.dismissDialog.call()
        })
    }

    private fun initData(addDeviceBindResponse: AddDeviceBindResponse?) {
        addDeviceBindResponse?.let {
            CanPointSp.deviName = it.deviceName
            CanPointSp.patternType = CanPointSp.FREE_MODE
            CanPointSp.store = it.storeName
            CanPointSp.storeId = it.storeId
            CanPointSp.password = it.password
            CanPointSp.authorization = it.authorization
            Utils.updateAuthorization(it.authorization)
        }
        startClass.call()
    }
}