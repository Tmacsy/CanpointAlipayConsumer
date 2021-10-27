package com.sam.canpoint.ecard.ui.init

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.api.request.AddDeviceBindRequest
import com.sam.canpoint.ecard.databinding.ActivityBindEcardAddressBinding
import android.os.SystemProperties
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.dialog.widget.ActionSheetDialog
import com.sam.utils.device.DeviceUtils
import com.sam.utils.network.NetworkUtils
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class BindAddressActivity : BaseRegisterActivity<ActivityBindEcardAddressBinding, BindAddressViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_bind_ecard_address
    override fun getViewModel(): BindAddressViewModel = createVM(BindAddressViewModel::class.java)
    private val storeInfo = ArrayList<GetStoreInfoResponse>()
    private val storeString = ArrayList<String>()
    private var storeDialog: ActionSheetDialog? = null
    private val addDeviceBindRequest = AddDeviceBindRequest()

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        mBinding.tvNext.setOnClickListener(this)
        mBinding.tvBack.setOnClickListener(this)
        mBinding.llBuildStore.setOnClickListener(this)
        observer()
        mViewModel.getStoreInfo()
    }

    private fun observer() {
        mViewModel.run {
            storeListLiveData.observe(this@BindAddressActivity, Observer {
                storeInfo.clear()
                storeInfo.addAll(it)
                for (response in storeInfo) {
                    storeString.add(response.storeName)
                }
            })
            startClass.observe(this@BindAddressActivity, Observer {
                SyncDataActivity.start(this@BindAddressActivity, false)
            })
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tv_next -> addDeviceBind()
            R.id.ll_build_store -> initBuildStore()
            R.id.tv_back -> finish()
        }
    }

    private fun initBuildStore() {
        if (storeInfo.isNullOrEmpty()) return
        if (storeDialog == null) {
            storeDialog = ActionSheetDialog(this, storeString.toTypedArray(), null)
            storeDialog?.isTitleShow(false)
            storeDialog?.onDialogItemClickListener { _, _, _, position, _ ->
                try {
                    mViewModel.storeName.value = if (TextUtils.isEmpty(storeInfo[position].storeName)) "" else storeInfo[position].storeName
                    addDeviceBindRequest.storeId = storeInfo[position].storeId
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        storeDialog?.show()
    }

    private fun addDeviceBind() {
        mViewModel.addDeviceBind(addDeviceBindRequest.apply {
            consumerModel = CanPointSp.FREE_MODE
            sn = DeviceUtils.getAndroidID()
            schoolCode = CanPointSp.schoolCode
            ipAddress = NetworkUtils.getIPAddress(true)
            macAddress = DeviceUtils.getMacAddress()
            deviceType = "7" //消费机类型
            deviceName = mViewModel.deviceName.value?.trim()
            itemId = SystemProperties.get("ro.product.model")
            supplierSn = SystemProperties.get("ro.product.board")
            operateType = "2" //注册设备
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        storeDialog?.dismiss()
    }
}