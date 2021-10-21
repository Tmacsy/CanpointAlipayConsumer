package com.sam.canpoint.ecard.application

import com.sam.app.SamApplication
import com.sam.canpoint.ecard.api.IPayApi
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.sam.http.ApiManager
import com.sam.http.SamApiManager
import com.sam.system.crash.sam.SamCrashHandler
import com.sam.utils.app.ProcessUtils
import com.tencent.bugly.crashreport.CrashReport
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

class CanPointECardApplication : SamApplication() {

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        if (ProcessUtils.isMainProcess()) {
            startBugLy()
        }
    }

    private fun startBugLy() {
        val userStrategy = CrashReport.UserStrategy(this)
        CrashReport.initCrashReport(this, "", true, userStrategy)
    }

    override fun configCrashHandler(): SamCrashHandler.CrashConfig? {
        return null
    }

    override fun configApi(): ApiManager.ApiConfig {
        val config = ApiManager.ApiConfig(this)
                .setEnableLog(true)
                .setAuthorization(CanPointSp.authorization)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .setDefaultBaseUrl(CanPointSp.serviceAddress)
        SamApiManager.init(config)
        //单独的给支付接口设置五秒超时，产品定义的
        SamApiManager.getInstance().setService(IPayApi::class.java, config.setConnectTimeOutSeconds(5)
                .setReadTimeOutSeconds(5).setWriteTimeOutSeconds(5));
        return config
    }

    override fun provideDesignHeightInDb(): Int = 682

    override fun provideDesignWidthInDb(): Int = 400

    companion object {
        private lateinit var sInstance: CanPointECardApplication
        fun the(): CanPointECardApplication {
            return sInstance
        }
    }
}