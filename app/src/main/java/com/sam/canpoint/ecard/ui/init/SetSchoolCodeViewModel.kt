package com.sam.canpoint.ecard.ui.init

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.tyx.base.mvvm.BaseViewModel
import com.tyx.base.mvvm.livedata.SingleLiveEvent

class SetSchoolCodeViewModel : BaseViewModel<SetSchoolCodeModel>() {
    override fun createModel(): SetSchoolCodeModel = SetSchoolCodeModel()

    val serviceAdd = MutableLiveData<String>()
    val schoolCode = MutableLiveData<String>()
    val startClass = SingleLiveEvent<Class<out Activity>>()

    init {
        serviceAdd.value = CanPointSp.serviceAddress
    }

    fun getSchoolName() {
        val address = serviceAdd.value
        val schoolCode = schoolCode.value
        if (address.isNullOrEmpty() || schoolCode.isNullOrEmpty()) {
            viewChange.showToast.value = "请输入内容!"
            return
        }
        viewChange.showLoadingDialog.call()
        model?.getSchoolName(address, schoolCode, success = {
            CanPointSp.schoolCode = schoolCode
            CanPointSp.schoolName = if (it.isNullOrEmpty()) "" else it
            startClass.value = BindAddressActivity::class.java
        }, error = {
            viewChange.showToast.value = it?.message
        }, complete = {
            viewChange.dismissDialog.call()
        })
    }
}