package com.sam.canpoint.ecard.ui.init

import android.app.Activity
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.system.log.L
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
            if (it != null && !TextUtils.isEmpty(it.name)) {
                CanPointSp.schoolCode = schoolCode
                CanPointSp.schoolName = it.name
                startClass.value = BindAddressActivity::class.java
            } else {
                viewChange.showToast.value = "学校编码错误!"
            }
        }, error = {
            viewChange.showToast.value = it?.message
        }, complete = {
            viewChange.dismissDialog.call()
        })
    }
}