package com.sam.canpoint.ecard.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationNetworkSettingBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.tyx.base.mvvm.ktx.createVM

class NetworkSettingPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {
    private lateinit var mBinding: PresentationNetworkSettingBinding
    private lateinit var mViewModel: NetWorkSettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_network_setting, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as NetWorkSettingActivity).createVM(NetWorkSettingViewModel::class.java)
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        if (event.type == KeyBoardType.SETTING) {
            if (!mBinding.llConfiguringNetwork.isShown) {
                mBinding.llConfiguringNetwork.visibility = View.VISIBLE
                mBinding.llStartConfigureNetwork.visibility = View.GONE
                mViewModel.startWifiSettings.call()
            }
            return true
        }
        return super.interceptKeyEvent(event)
    }
}