package com.sam.canpoint.ecard.ui.home

import android.text.TextUtils
import com.alibaba.fastjson.JSONObject
import com.alipay.zoloz.smile2pay.ZolozConfig
import com.sam.canpoint.ecard.api.ICanPointApi
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.ui.model.AliPayBaseModel
import com.sam.canpoint.ecard.utils.CanPointSp
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

    /**
     * 刷脸参数配置
     * faceModel = 1 自由模式 2 定额模式
     */
    fun mockConfigInfo(faceModel: Int, price: String): Map<String, Any> {
        val map = HashMap<String, Any>()
        return map.apply {
            val configInfo = JSONObject() //设置参数配置-start
            configInfo[ZolozConfig.KEY_MODE_FACE_MODE] = ZolozConfig.FaceMode.FACEPAY // 必填项，模式
            configInfo[ZolozConfig.KEY_ALGORITHM_MAX_DETECT_DISTANCE] = 750 // 必填项，取值：0～1000mm，建议值：750mm
            put(ZolozConfig.KEY_ZOLOZ_CONFIG, configInfo.toJSONString())
            //设置参数配置-end
            // -------------------------------------------------------------------------------------
            //UI参数配置-start
            val uiConfig = JSONObject()
            when (faceModel) {
                //自由模式
                1 -> {
                    uiConfig[ZolozConfig.KEY_UI_SHOW_PAYMENT_CODE] = false //是否显示"付款码支付"按钮，true:显示，默认false
                    uiConfig[ZolozConfig.KEY_UI_PAY_AMOUNT] = price.toFloat() //自由模式，支付金额，可选是否显示，非必须
                }
                //定额模式
                2 -> {
                    uiConfig[ZolozConfig.KEY_UI_SHOW_PAYMENT_CODE] = false //是否显示"付款码支付"按钮，true:显示，默认false
                    uiConfig[ZolozConfig.KEY_UI_PAY_AMOUNT] = price.toFloat() //定额模式，支付金额，必填项
                    uiConfig[ZolozConfig.KEY_UI_ENABLE_TIME_OUT] = false //定额模式，必填项，采集页面否启用超时机制(boolean)，false：禁用超时机制。默认：true
                }
            }
            put(ZolozConfig.KEY_UI_CONFIG, uiConfig.toJSONString())
            //UI参数配置-end
            //--------------------------------------------------------------------------------------
            //主,副评显示 默认主频  SMILE_MODE_EXT_DISPLAY副屏
            put(ZolozConfig.KEY_SMILE_MODE, ZolozConfig.SmileMode.SMILE_MODE_DEFAULT_DISPLAY)
        }
    }

    private var flag = true

    fun pwdVerify(pwd: String, success: (String) -> Unit, error: (Throwable?) -> Unit) {
        if (!flag) return
        flag = false
        val map = HashMap<String, String>()
        SamApiManager.getInstance().getService(ICanPointApi::class.java).verifyPassword(map.apply {
            put("password", pwd)
            put("schoolCode", CanPointSp.schoolCode)
        }).compose(applySchedulers(object : BaseRxObserver<String>(this) {
            override fun onSuccess(d: String?, message: String?) {
                success.invoke("success")
            }

            override fun onFail(e: Throwable?) {
                if (!TextUtils.isEmpty(map["password"]) && map["password"].equals(CanPointSp.password)){
                    success.invoke("success")
                } else {
                    error.invoke(e)
                }
            }

            override fun onComplete() {
                super.onComplete()
                flag = true
            }
        }))
    }
}