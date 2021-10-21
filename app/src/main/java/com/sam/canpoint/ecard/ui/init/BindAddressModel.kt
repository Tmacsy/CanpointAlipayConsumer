package com.sam.canpoint.ecard.ui.init

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.sam.http.SamApiManager
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.model.BaseModel
import com.tyx.base.mvvm.observer.BaseRxObserver

class BindAddressModel : BaseModel() {


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
}