package com.sam.canpoint.ecard.ui.splash

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.alipay.iot.sdk.APIManager
import com.sam.canpoint.ecard.api.bean.AddDeviceBindResponse
import com.sam.canpoint.ecard.api.bean.GetConsumeRuleResponse
import com.sam.canpoint.ecard.api.bean.MerchantInfoBean
import com.sam.canpoint.ecard.api.bean.SplashZipData
import com.sam.canpoint.ecard.application.CanPointECardApplication
import com.sam.canpoint.ecard.manager.VoiceManager
import com.sam.canpoint.ecard.ui.home.HomeActivity
import com.sam.canpoint.ecard.ui.init.InitDeviceActivity
import com.sam.canpoint.ecard.ui.init.NetworkConfigurationActivity
import com.sam.canpoint.ecard.ui.model.SyncAccountThread
import com.sam.canpoint.ecard.utils.Utils
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.system.log.L
import com.sam.utils.device.DeviceUtils
import com.sam.utils.network.NetworkUtils
import com.tencent.bugly.crashreport.CrashReport
import com.tyx.base.mvvm.BaseViewModel
import io.reactivex.rxjava3.disposables.Disposable

class SplashViewModel : BaseViewModel<SplashModel>() {
    override fun createModel(): SplashModel = SplashModel()
    val startClass = MutableLiveData<Class<out Activity>>()
    private var zipDisposable: Disposable? = null
    private var merchantInfoBean: MerchantInfoBean? = null
    private var mHandler = Handler(Looper.getMainLooper())

    fun initSplash() {
        Utils.resetFourRandomMinute()
        if (CanPointSp.appRunCount == 0) {
            //第一次运行
            startClass.value =
                    if (NetworkUtils.isConnected()) InitDeviceActivity::class.java else NetworkConfigurationActivity::class.java
        } else {
            model?.clearOverdueLocalRecord()
            if (NetworkUtils.isNetAvailable()) {
                model?.reportOfflineRecords()
                getData()
            } else {
                VoiceManager.get().voice("e15")
                startClass.value = HomeActivity::class.java
            }
        }
    }

    private fun getData() {
        model?.getData(success = {
            initData(it)
        }, error = {
            L.e("splash合并请求数据出错${it.message}")
            viewChange.showToast.value = "获取数据失败!"
        }, disposable = {
            zipDisposable = it
        }, complete = {
            L.d("splash合并请求结束!")
            initIOT(merchantInfoBean)
            syncAccount()
        })
    }

    private fun initData(data: SplashZipData?) {
        if (data == null) {
            viewChange.showToast.value = "获取数据失败!"
            return
        }
        initConsumer(data.list)
        initApi(data.addDeviceBindResponse)
        merchantInfoBean = data.merchantInfoBean
    }

    private fun initConsumer(list: ArrayList<GetConsumeRuleResponse>) {
        if (list.isNullOrEmpty()) return
        try {
            val dao = SamDBManager.getInstance().dao(GetConsumeRuleResponse::class.java)
            dao.clearTable()
            for (getConsumeRuleResponse in list) {
                dao.addOrUpdate(getConsumeRuleResponse)
                CanPointSp.run {
                    maxSingleConsume = getConsumeRuleResponse.singleConsume.toString()
                    offlinePopupNum = getConsumeRuleResponse.offlinePopupNum
                    offlineStopNum = getConsumeRuleResponse.offlineStopNum
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initApi(data: AddDeviceBindResponse) {
        val authorization = data.authorization
        if (!authorization.isNullOrEmpty()) {
            CanPointSp.authorization = authorization
            Utils.updateAuthorization(authorization)
        }
        if (!data.password.isNullOrEmpty()) {
            CanPointSp.password = data.password
        }
    }

    private fun initIOT(data: MerchantInfoBean? = null) {
        var infoBean = data
        if (infoBean == null) {
            val queryOne = SamDBManager.getInstance().dao(MerchantInfoBean::class.java).queryOne(WhereInfo.get())
            if (queryOne != null) infoBean = queryOne
        }
        if (infoBean != null) {
            APIManager.getInstance().initialize(CanPointECardApplication.the(), infoBean.isvPid) {
                CanPointSp.iotStatus = it && !APIManager.getInstance().deviceAPI.deviceId.isNullOrEmpty()
                if (it) {
                    L.d("IOT初始化成功...")
                } else {
                    L.e("IOT初始化失败!")
                    VoiceManager.get().voice("e9")
                    CrashReport.postCatchedException(Throwable("iot初始化失败的异常,设备SN=" + DeviceUtils.getAndroidID()))
                }
                SamDBManager.getInstance().dao(MerchantInfoBean::class.java).addOrUpdate(infoBean)
            }
        }
    }

    private fun syncAccount() {
        val thread = SyncAccountThread(false)
        thread.run {
            setSyncCallback(object : SyncAccountThread.SyncAccountCallback {
                override fun complete() {
                    mHandler.post {
                        startClass.value = HomeActivity::class.java
                    }
                }
            })
            start()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        zipDisposable?.dispose()
    }
}