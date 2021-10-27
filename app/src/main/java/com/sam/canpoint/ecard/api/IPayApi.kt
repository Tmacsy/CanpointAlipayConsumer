package com.sam.canpoint.ecard.api

import com.sam.canpoint.ecard.api.bean.PayResultBean
import com.sam.canpoint.ecard.api.request.FacePayEntity
import com.tyx.base.bean.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface IPayApi {
    /**
     * 刷脸付
     */
    @POST("efacepay/facePay")
    fun facePay(@Body entity: FacePayEntity): Observable<BaseResponse<PayResultBean>>
}