package com.sam.canpoint.ecard.ui.setting

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.KeyEvent
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityNetworkSettingBinding
import com.sam.canpoint.ecard.keyboard.IKeyBoardCallback
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardManager
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.widget.header_bar.BaseHeaderView
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class NetWorkSettingActivity : BaseMVVMActivity<ActivityNetworkSettingBinding, NetWorkSettingViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_network_setting
    override fun getViewModel(): NetWorkSettingViewModel = createVM(NetWorkSettingViewModel::class.java)
    private val keyBoardManager = KeyBoardManager()
    private var mPresentation: BasePresentation? = null

    override fun initView() {
        super.initView()
        mBinding.leftImgTitleBar.setOnHeaderClickListener(object : BaseHeaderView.OnHeaderClickListener {
            override fun onHeaderLeftClicked() {
                finish()
            }

            override fun onHeaderRightClicked() {
            }
        })
        mBinding.llWifiSetting.setOnClickListener { startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
        showPresentation()
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
            if (event.type == KeyBoardType.SETTING) {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            } else if (event.type == KeyBoardType.CANCEL) {
                finish()
            }
        }
    }

    private fun showPresentation() {
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.NETWORK_SETTING)
        mPresentation?.showPresentation()
    }

    override fun onNetWorkChange(state: Int) {
        mPresentation?.showNetworkImg(state.presenterNetWorkStr(), state.presentationNetWorkImg())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyBoardManager.onKeyDown(keyCode, event)
    }

    companion object {
        fun start(mContext: Context) {
            val intent = Intent(mContext, NetWorkSettingActivity::class.java)
            mContext.startActivity(intent)
        }
    }
}