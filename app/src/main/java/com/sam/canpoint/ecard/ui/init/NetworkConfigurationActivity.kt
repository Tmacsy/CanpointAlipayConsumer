package com.sam.canpoint.ecard.ui.init

import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityNetworkConfigurationBinding
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class NetworkConfigurationActivity : BaseMVVMActivity<ActivityNetworkConfigurationBinding,NetworkConfigurationViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_network_configuration

    override fun getViewModel(): NetworkConfigurationViewModel = createVM(NetworkConfigurationViewModel::class.java)

    override fun initView() {
        super.initView()

    }
}