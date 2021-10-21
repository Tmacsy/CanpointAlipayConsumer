package com.sam.canpoint.ecard.ui.init

import android.content.Intent
import android.view.View
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityNetworkConfigurationBinding
import com.sam.utils.network.NetworkUtils
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class NetworkConfigurationActivity : BaseMVVMActivity<ActivityNetworkConfigurationBinding, NetworkConfigurationViewModel>() {

    override fun getLayoutId(): Int = R.layout.activity_network_configuration

    override fun getViewModel(): NetworkConfigurationViewModel = createVM(NetworkConfigurationViewModel::class.java)

    override fun initView() {
        super.initView()
        mBinding.run {
            wired.setOnClickListener(this@NetworkConfigurationActivity)
            wireless.setOnClickListener(this@NetworkConfigurationActivity)
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.wired -> {
                val intent = Intent(android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.wireless -> {
                val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (NetworkUtils.isNetAvailable()) {
            val intent = Intent(this,InitDeviceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}