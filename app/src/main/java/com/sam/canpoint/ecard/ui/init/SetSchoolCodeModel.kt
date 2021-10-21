package com.sam.canpoint.ecard.ui.init

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.IPayApi
import com.sam.canpoint.ecard.api.bean.GetSchoolNameBySchoolCodeResponse
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.sam.http.SamApiManager
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.model.BaseModel
import com.tyx.base.mvvm.observer.BaseRxObserver

class SetSchoolCodeModel : BaseModel() {

    fun getSchoolName(serviceAddress: String, schoolCode: String, success: (String?) -> Unit, error: (Throwable?) -> Unit, complete: () -> Unit) {
        var address = serviceAddress
        if (!address.endsWith("/")) address += "/"
        SamApiManager.getInstance().setServiceBaseUrl(ICanPointApi::class.java, address)
        SamApiManager.getInstance().setServiceBaseUrl(IPayApi::class.java, address)
        CanPointSp.serviceAddress = address
        SamApiManager.getInstance().getService(ICanPointApi::class.java).getSchoolNameBySchoolCode(schoolCode)
                .compose(applySchedulers(object : BaseRxObserver<GetSchoolNameBySchoolCodeResponse>(this) {
                    override fun onSuccess(d: GetSchoolNameBySchoolCodeResponse?, message: String?) {
                        success.invoke(d?.name)
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