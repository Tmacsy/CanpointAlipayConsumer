package com.sam.canpoint.ecard.ui.init

import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityInitDeviceBinding
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class InitDeviceActivity : BaseMVVMActivity<ActivityInitDeviceBinding,InitDeviceViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_init_device

    override fun getViewModel(): InitDeviceViewModel = createVM(InitDeviceViewModel::class.java)

    override fun initView() {
        super.initView()

    }
}