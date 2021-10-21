package com.sam.canpoint.ecard.api

import com.sam.canpoint.ecard.api.bean.AddDeviceBindResponse
import com.sam.canpoint.ecard.api.bean.GetAccountInfoListResponse
import com.sam.canpoint.ecard.api.bean.GetConsumeRuleResponse
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.api.request.AddDeviceBindRequest
import com.tyx.base.bean.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ICanPointApi {

    /**
     * 获取学校消费规则
     */
    @POST("api/consume/queryConsumeRule")
    fun getConsumerRule(@Query("schoolCode") schoolCode: String): Observable<BaseResponse<ArrayList<GetConsumeRuleResponse>>>

    /**
     * 消费机注册
     */
    @POST("api/consume/addDeviceBind")
    fun addDeviceBind(@Body request: AddDeviceBindRequest): Observable<BaseResponse<AddDeviceBindResponse>>

    /**
     * 获取初始化相关数据
     */
    @POST("efacepay/initMerchantInfo")
    fun merchantInfo(): Observable<BaseResponse<MerchantInfoBean>>

    /**
     * 通过学校查询阿里账户信息
     *
     * @param schoolCode 学校编码
     */
    @POST("api/consume/queryAliAccountInfo")
    fun getAccountInfoList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int, @Query("schoolCode") schoolCode: String): Observable<BaseResponse<ArrayList<GetAccountInfoListResponse>>>
}