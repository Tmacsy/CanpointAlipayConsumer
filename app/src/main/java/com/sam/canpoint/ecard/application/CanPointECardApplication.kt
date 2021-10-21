package com.sam.canpoint.ecard.application

import com.sam.app.SamApplication
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
            .setConnectTimeOutSeconds(5)
            .setAuthorization(CanPointSp.authorization)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .setDefaultBaseUrl("http://tm.canpointlive.com:11101/")
        SamApiManager.init(config)
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