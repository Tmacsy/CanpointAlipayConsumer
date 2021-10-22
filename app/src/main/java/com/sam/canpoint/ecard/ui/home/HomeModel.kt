package com.sam.canpoint.ecard.ui.home

import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.http.SamApiManager
import com.tyx.base.mvvm.exception.ExceptionHandle
import com.tyx.base.mvvm.ktx.applySchedulers
import com.tyx.base.mvvm.observer.BaseRxObserver

class HomeModel : AliPayBaseModel() {

    fun merchantInfo(isNet: Boolean = true, success: (MerchantInfoBean) -> Unit, error: (Throwable?) -> Unit) {
        if (isNet) {
            SamApiManager.getInstance().getService(ICanPointApi::class.java).merchantInfo()
                    .compose(applySchedulers(object : BaseRxObserver<MerchantInfoBean>(this) {
                        override fun onSuccess(d: MerchantInfoBean, message: String?) {
                            success.invoke(d)
                        }

                        override fun onFail(e: Throwable?) {
                            //失败
                            merchantInfo(false, success, error)
                        }
                    }))
        } else {
            val queryOne = SamDBManager.getInstance().dao(MerchantInfoBean::class.java).queryOne(WhereInfo.get())
            if (queryOne != null) {
                success.invoke(queryOne)
            } else {
                error.invoke(ExceptionHandle.ResponseThrowable("获取商户信息失败!", -10))
            }
        }
    }
}