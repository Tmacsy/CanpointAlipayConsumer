package com.sam.canpoint.ecard.api

import com.sam.canpoint.ecard.api.bean.*
import com.sam.canpoint.ecard.api.request.AddDeviceBindRequest
import com.tyx.base.bean.BaseResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Call
import retrofit2.http.*

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
    fun getAccountInfoList(@Query("pageNum") pageNum: Int, @Query("pageSize") pageSize: Int, @Query("schoolCode") schoolCode: String): Call<AccountInfoBean>


    /**
     * 根据学校编码拉取学校名称
     */
    @POST("api/consume/querySchool")
    fun getSchoolNameBySchoolCode(@Query("schoolCode") schoolCode: String?): Observable<BaseResponse<GetSchoolNameBySchoolCodeResponse>>

    /**
     * 查询档口下拉列表
     */
    @GET("api/consume/selectStore")
    fun getStoreBySchoolCode(@Query("schoolCode") schoolCode: String?): Observable<BaseResponse<ArrayList<GetStoreInfoResponse>>>

    /**
     * 获取学校消费规则
     */
    @GET("api/consume/initDevice")
    fun initDevice(@Query("sn") sn: String?): Observable<BaseResponse<InitDeviceResponse>>

    /**
     * 验证密码
     */
    @POST("api/consume/verifyPassword")
    fun verifyPassword(@QueryMap map: Map<String, String>): Observable<BaseResponse<String>>

    /**
     * 通过订单id查询订单详情
     */
    @GET("api/consume/queryOrderDetail")
    fun queryOrderDetail(@Query("orderId") orderId: String): Observable<BaseResponse<OrderDetailResponse>>

    /**
     * 退款
     */
    @POST("api/consume/refund")
    fun refund(@Query("orderId") orderId: String): Observable<BaseResponse<RefundResultBean>>

    /**
     * 消费机更新
     */
    @POST("api/consume/updateDeviceBind")
    fun updateDeviceBind(@Query("consumerModel") consumerModel: String?, @Query("deviceName") deviceName: String?,
                         @Query("schoolCode") schoolCode: String?, @Query("storeId") storeId: String?): Observable<BaseResponse<String>>

    /**
     * 修改消费模式
     */
    @POST("api/consume/updateDeviceBind")
    fun changeDeviceBind(@QueryMap map: Map<String, String>): Observable<BaseResponse<String>>

    /**
     * 消费机当日统计
     */
    @GET("api/consume/deviceStatisticsByDay")
    fun getDeviceStatisticsByDay(): Observable<BaseResponse<StatisticsByDayResponse>>

    /**
     * 修改设备密码
     */
    @POST("api/consume/updatePassword")
    fun updatePassword(@Query("newPwd") newPwd: String): Observable<BaseResponse<String>>
}