package com.sam.canpoint.ecard.ui.init

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.AddDeviceBindResponse
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.api.request.AddDeviceBindRequest
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.http.SamApiManager
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver

class BindAddressModel : AliPayBaseModel() {


    fun getStore(success: (ArrayList<GetStoreInfoResponse>?) -> Unit, error: (Throwable?) -> Unit) {
        SamApiManager.getInstance().getService(ICanPointApi::class.java).getStoreBySchoolCode(CanPointSp.schoolCode)
                .compose(applySchedulers(object : BaseRxObserver<ArrayList<GetStoreInfoResponse>>(this) {
                    override fun onSuccess(d: ArrayList<GetStoreInfoResponse>?, message: String?) {
                        success.invoke(d)
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }
                }))
    }

    fun addDeviceBind(request: AddDeviceBindRequest, success: (AddDeviceBindResponse?) -> Unit, error: (Throwable?) -> Unit, complete: () -> Unit) {
        SamApiManager.getInstance().getService(ICanPointApi::class.java).addDeviceBind(request)
                .compose(applySchedulers(object : BaseRxObserver<AddDeviceBindResponse>(this) {
                    override fun onSuccess(d: AddDeviceBindResponse?, message: String?) {
                        success.invoke(d)
                    }

                    override fun onFail(e: Throwable?) {
                        error.invoke(e)
                    }

                    override fun onComplete() {
                        super.onComplete()
                        complete.invoke()
                    }
                }))
    }
}