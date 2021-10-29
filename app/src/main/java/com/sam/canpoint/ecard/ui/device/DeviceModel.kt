package com.sam.canpoint.ecard.ui.device

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.ui.init.BindAddressModel
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.http.SamApiManager
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver

class DeviceModel : BindAddressModel() {

    fun updateDeviceBind(devicesName: String, success: (String?) -> Unit, error: (Throwable?) -> Unit, complete: () -> Unit = {}) {
        SamApiManager.getInstance().getService(ICanPointApi::class.java)
                .updateDeviceBind(null, devicesName, CanPointSp.schoolCode, CanPointSp.storeId)
                .compose(applySchedulers(object : BaseRxObserver<String>(this) {
                    override fun onSuccess(d: String?, message: String?) {
                        success.invoke(d)
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }

                    override fun onComplete() {
                        complete.invoke()
                    }
                }))
    }
}