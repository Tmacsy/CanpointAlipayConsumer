package com.sam.canpoint.ecard.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityHomeBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardManager
import com.sam.canpoint.ecard.reveiver.TimeChangeReceiver
import com.sam.canpoint.ecard.ui.presentation.IPresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.utils.common.TimeUtils
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class HomeActivity : BaseMVVMActivity<ActivityHomeBinding, HomeViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_home
    override fun getViewModel(): HomeViewModel = createVM(HomeViewModel::class.java)
    private val keyBoardManager = KeyBoardManager()
    private var timeChangeReceiver: TimeChangeReceiver? = null
    private val presentationMap = HashMap<String, IPresentation>()

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        setEnableNetworkCheck(true)
        observer()
        timeReceiver()
        initSmile()
    }

    private fun observer() {
        mViewModel.run {

        }
    }

    private fun initSmile() {

    }

    private fun timeReceiver() {
        val filter = IntentFilter()
        timeChangeReceiver = TimeChangeReceiver()
        timeChangeReceiver?.setTimeChangeCallback(object : TimeChangeReceiver.TimeChangeCallback {
            override fun timeChange() {
            }
        })
        registerReceiver(timeChangeReceiver, filter.apply {
            addAction(Intent.ACTION_TIME_TICK)
        })
    }

    private fun showPresentation(type: String?) {
        type?.let {
            val factory = PresentationFactory.getFactory()
            if (!presentationMap.containsKey(it)) {
                presentationMap[it] = factory.createPresentation(this, type)
            }
            val presentation = presentationMap[it]
            presentation?.showPresentation()
        }
    }

    private fun cancelPresentation(type: String) {
        if (presentationMap.containsKey(type)) {
            val presentation = presentationMap[type]
            presentation?.cancelPresentation()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeChangeReceiver?.let { unregisterReceiver(it) }
    }

    override fun onNetWorkChange(state: Int) {
        mViewModel.netWorkState.value = state
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyBoardManager.onKeyDown(keyCode, event)
    }
}