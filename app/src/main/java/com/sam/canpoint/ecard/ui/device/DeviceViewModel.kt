package com.sam.canpoint.ecard.ui.device

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.SystemProperties
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.utils.SamUtils
import com.sam.utils.device.DeviceUtils
import com.tyx.base.mvvm.BaseViewModel
import java.util.*

class DeviceViewModel : BaseViewModel<DeviceModel>() {

    override fun createModel(): DeviceModel = DeviceModel()
    val deviceName = MutableLiveData<String>()
    val bindStore = MutableLiveData<String>()
    val netWorkState = MutableLiveData<Int>() //网络状态
    val deviceSn = MutableLiveData<String>()
    val systemVersion = MutableLiveData<String>()
    val serviceAddress = MutableLiveData<String>()
    val faceVersion = MutableLiveData<String>()
    val storeListLiveData = MutableLiveData<ArrayList<GetStoreInfoResponse>>()

    init {
        deviceName.value = CanPointSp.devicesName
        bindStore.value = CanPointSp.store
        deviceSn.value = DeviceUtils.getAndroidID()
        systemVersion.value = SystemProperties.get("ro.alipayiot.build.version", "")
        serviceAddress.value = CanPointSp.serviceAddress
        try {
            val packageInfo: PackageInfo = SamUtils.getApp().packageManager.getPackageInfo("com.alipay.zoloz.smile", 0)
            faceVersion.value = String.format(Locale.getDefault(), "%s-%d", packageInfo.versionName, packageInfo.versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    fun getStoreInfo() {
        model?.getStore(success = {
            if (it.isNullOrEmpty()) return@getStore
            storeListLiveData.value = it
        }, error = {
            viewChange.showToast.value = it?.message
        })
    }

    fun updateDeviceBind(trim: String, storeId: String? = null, store: String? = null) {
        if (TextUtils.isEmpty(trim)) {
            viewChange.showToast.value = "设备名称不能为空!"
            return
        }
        if (trim == CanPointSp.devicesName) return
        viewChange.showLoadingDialog
        model?.updateDeviceBind(trim, success = {
            if (CanPointSp.devicesName != trim) {
                deviceName.value = trim
                CanPointSp.devicesName = trim
            }
            if (!storeId.isNullOrEmpty()) {
                bindStore.value = store
                CanPointSp.storeId = storeId
                CanPointSp.store = store ?: CanPointSp.store
            }
        }, error = {
            viewChange.showToast.value = it?.message
        }, complete = {
            viewChange.dismissDialog
        })
    }
}