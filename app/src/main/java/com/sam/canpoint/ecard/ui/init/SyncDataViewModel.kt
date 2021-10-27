package com.sam.canpoint.ecard.ui.init

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.api.bean.*
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.ui.model.SyncAccountThread
import com.sam.canpoint.ecard.ui.model.setCallback
import com.sam.canpoint.ecard.utils.Utils
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.db.SamDBManager
import com.sam.system.log.L
import com.tyx.base.mvvm.BaseViewModel
import io.reactivex.rxjava3.disposables.Disposable

class SyncDataViewModel : BaseViewModel<SyncDataModel>() {
    override fun createModel(): SyncDataModel = SyncDataModel()
    private var syncDisposable: Disposable? = null
    private var mHandler = Handler(Looper.getMainLooper())
    private var thread: SyncAccountThread? = null
    val success = MutableLiveData<String>()
    val error = MutableLiveData<String>()

    fun cleanDB() {
        cleanDB(MerchantInfoBean::class.java)
        cleanDB(ConsumptionLocalRecordsRequest::class.java)
        cleanDB(GetConsumeRuleResponse::class.java)
        cleanDB(GetAccountInfoListResponse::class.java)
    }

    private fun <T> cleanDB(clazz: Class<T>) {
        SamDBManager.getInstance().dao(clazz).clearTable()
    }

    fun autoInitDevice() {
        model?.autoInitDevice(success = {
            initAutoData(it)
        }, error = {
            L.e("自动初始化失败!${it?.message}")
            error.value = "注册中导入数据失败!${it?.message ?: "未知"}"
        }, complete = {

        }, disposable = {
            syncDisposable = it
        })
    }

    private fun initAutoData(response: AutoInitDeviceZipData?) {
        if (response == null) {
            error.value = "获取数据为空!"
            return
        }
        val initDeviceResponse = response.initDeviceResponse
        val consumerMachine = initDeviceResponse.consumerMachine
        val consumeRuleList = initDeviceResponse.consumeRuleList
        with(CanPointSp) {
            schoolCode = consumerMachine.schoolCode
            schoolName = initDeviceResponse.schoolName
            deviName = consumerMachine.deviceName
            patternType = FREE_MODE
            store = consumerMachine.storeName
            storeId = consumerMachine.storeId
            password = consumerMachine.password
            authorization = consumerMachine.authorization
            Utils.updateAuthorization(authorization)
        }
        for (data in consumeRuleList) {
            SamDBManager.getInstance().dao(GetConsumeRuleResponse::class.java).addOrUpdate(data)
        }
        val merchantInfoBean = response.merchantInfoBean
        initIOT(merchantInfoBean)
    }

    fun getData() {
        model?.getData(success = {
            initData(it)
        }, error = {
            L.e("手动导入合并请求数据出错${it.message}")
            error.value = "获取数据失败!"
        }, complete = {
            L.d("手动请求完毕")
        }, disposable = {
            syncDisposable = it
        })
    }

    private fun initData(data: SplashZipData?) {
        if (data == null) {
            error.value = "获取数据为空!"
            return
        }
        initApi(data.addDeviceBindResponse)
        initConsumer(data.list)
        initIOT(data.merchantInfoBean)
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

    private fun initIOT(data: MerchantInfoBean?) {
        if (data != null) {
            model?.initIOT(data, result = {
                syncAccount()
            })
        }
    }

    private fun syncAccount() {
        thread = SyncAccountThread(true)
        thread?.run {
            setCallback(success = {
                mHandler.post {
                    success.value = "success"
                }
            }, error = {
                mHandler.post {
                    error.value = it
                }
            })
            start()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        syncDisposable?.dispose()
        thread?.setSyncCallback(null)
        thread = null
    }
}