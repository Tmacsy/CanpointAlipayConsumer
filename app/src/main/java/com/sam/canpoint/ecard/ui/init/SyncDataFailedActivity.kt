package com.sam.canpoint.ecard.ui.init

import android.content.ComponentName
import android.content.Intent
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySynFailedBinding
import com.sam.utils.device.DeviceUtils
import com.tencent.bugly.crashreport.CrashReport
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SyncDataFailedActivity : BaseMVVMActivity<ActivitySynFailedBinding, SyncDataFailedViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_syn_failed

    override fun getViewModel(): SyncDataFailedViewModel = createVM(SyncDataFailedViewModel::class.java)

    private var isAuto = false

    override fun initView() {
        super.initView()
        val stringExtra = intent.getStringExtra("errorStr")
        isAuto = intent.getBooleanExtra("isAuto", isAuto)
        mBinding.tvError.text = stringExtra
        CrashReport.postCatchedException(Throwable("注册中导入数据失败，isAutoInit=${isAuto},errorSting=${stringExtra},设备SN=${DeviceUtils.getAndroidID()}"))
        mBinding.tvReload.setOnClickListener {
            SyncDataActivity.start(this, isAuto)
            finish()
        }
        mBinding.tvBack.setOnClickListener {
            val intent = Intent()
            val componentName: ComponentName = if (isAuto) {
                ComponentName(this, InitDeviceActivity::class.java)
            } else {
                ComponentName(this,BindAddressActivity::class.java)
            }
            intent.component = componentName
            startActivity(intent)
            finish()
        }
    }
}