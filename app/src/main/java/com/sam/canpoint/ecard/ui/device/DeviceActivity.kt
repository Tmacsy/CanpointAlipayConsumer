package com.sam.canpoint.ecard.ui.device

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.databinding.ActivityEcardDeviceInfoBinding
import com.sam.canpoint.ecard.keyboard.IKeyBoardCallback
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardManager
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.dialog.widget.ActionSheetDialog
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM
import java.util.*
import kotlin.collections.ArrayList

class DeviceActivity : BaseMVVMActivity<ActivityEcardDeviceInfoBinding, DeviceViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_ecard_device_info
    override fun getViewModel(): DeviceViewModel = createVM(DeviceViewModel::class.java)
    private lateinit var inputDialog: AlertDialog
    private val keyBoardManager = KeyBoardManager()
    private val storeInfo = ArrayList<GetStoreInfoResponse>()
    private val storeString = ArrayList<String>()
    private var storeDialog: ActionSheetDialog? = null
    private var mPresentation: BasePresentation? = null

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        initClick()
        observer()
        showPresentation()
        mViewModel.getStoreInfo()
    }


    private fun initClick() {
        mBinding.run {
            deviceName.setRightButtonClickListener { inputDeviceNameDialog() }
            bindingSite.setOnClickListener { showStoreDialog() }
            serverAddress.setRightButtonClickListener { inputServiceAddressDialog() }
        }
    }

    private fun inputDeviceNameDialog() {
        inputDialog = AlertDialog.Builder(this).create()
        inputDialog.run {
            setView(layoutInflater.inflate(R.layout.device_name_dialog, null))
            show()
            window?.setGravity(Gravity.CENTER)
            window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window?.setContentView(layoutInflater.inflate(R.layout.device_name_dialog, null))
            val etDeviceName = inputDialog.findViewById<EditText>(R.id.et_device_name)
            val btCancel = inputDialog.findViewById<Button>(R.id.btn_cancel)
            val btSure = inputDialog.findViewById<Button>(R.id.btn_sure)
            btCancel.setOnClickListener { inputDialog.dismiss() }
            btSure.setOnClickListener {
                mViewModel.updateDeviceBind(etDeviceName.text.toString().trim())
            }
        }
    }

    private fun inputServiceAddressDialog() {
        inputDialog = AlertDialog.Builder(this).create()
        inputDialog.run {
            setView(layoutInflater.inflate(R.layout.server_address_dialog, null))
            show()
            window?.setGravity(Gravity.CENTER)
            window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setContentView(layoutInflater.inflate(R.layout.server_address_dialog, null))
            val etServerAddress = findViewById<EditText>(R.id.et_server_address)
            etServerAddress.setText(CanPointSp.serviceAddress)
            etServerAddress.setSelection(CanPointSp.serviceAddress.length)
            val btCancel = findViewById<Button>(R.id.btn_cancel)
            val btSure = findViewById<Button>(R.id.btn_sure)
            btCancel.setOnClickListener { v: View? -> inputDialog.dismiss() }
            btSure.setOnClickListener { v: View? ->
                val inputServerAddress = etServerAddress.text.toString().trim()
                if (CanPointSp.serviceAddress != inputServerAddress) {
                    CanPointSp.serviceAddress = inputServerAddress
                    mViewModel.serviceAddress.value = inputServerAddress
                }
                inputDialog.dismiss()
            }
        }
    }

    private fun observer() {
        mViewModel.run {
            storeListLiveData.observe(this@DeviceActivity, Observer {
                storeInfo.clear()
                storeInfo.addAll(it)
                for (response in storeInfo) {
                    storeString.add(response.storeName)
                }
            })
        }
    }

    private fun showPresentation() {
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.DEVICES_INFO)
        mPresentation?.showPresentation()
    }

    override fun onResume() {
        super.onResume()
        keyBoardManager.keyBoardCallback = keyBoardCallback
    }

    override fun onPause() {
        super.onPause()
        keyBoardManager.keyBoardCallback = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresentation?.cancelPresentation()
    }

    private val keyBoardCallback = object : IKeyBoardCallback {
        override fun keyBoardEvent(event: KeyBoardEvent) {
            val result = mPresentation?.interceptKeyEvent(event) ?: false
            if (result) return
        }
    }

    private fun showStoreDialog() {
        if (storeInfo.isNullOrEmpty()) return
        if (storeDialog == null) {
            storeDialog = ActionSheetDialog(this, storeString.toTypedArray(), null)
            storeDialog?.isTitleShow(false)
            storeDialog?.onDialogItemClickListener { _, _, _, position, _ ->
                try {
                    mViewModel.bindStore.value = if (TextUtils.isEmpty(storeInfo[position].storeName)) "" else storeInfo[position].storeName
                    mViewModel.updateDeviceBind(mViewModel.deviceName.value
                            ?: CanPointSp.devicesName, storeInfo[position].storeId, if (TextUtils.isEmpty(storeInfo[position].storeName)) "" else storeInfo[position].storeName)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        storeDialog?.show()
    }

    override fun onNetWorkChange(state: Int) {
        super.onNetWorkChange(state)
        mViewModel.netWorkState.value = state
        mPresentation?.showNetworkImg(state.presenterNetWorkStr(), state.presentationNetWorkImg())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyBoardManager.onKeyDown(keyCode, event)
    }

    companion object {
        fun start(mContext: Context) {
            val intent = Intent(mContext, DeviceActivity::class.java)
            mContext.startActivity(intent)
        }
    }
}