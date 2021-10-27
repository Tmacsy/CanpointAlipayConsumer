package com.sam.canpoint.ecard.ui.home

import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import androidx.lifecycle.Observer
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityHomeBinding
import com.sam.canpoint.ecard.keyboard.IKeyBoardCallback
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardManager
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.reveiver.TimeChangeReceiver
import com.sam.canpoint.ecard.ui.pay.PayActivity
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory.INPUT_AMOUNT
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.canpoint.ecard.utils.CanPointSp
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.HashMap

class HomeActivity : BaseMVVMActivity<ActivityHomeBinding, HomeViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_home
    override fun getViewModel(): HomeViewModel = createVM(HomeViewModel::class.java)
    private val keyBoardManager = KeyBoardManager()
    private var timeChangeReceiver: TimeChangeReceiver? = null
    private val presentationMap = HashMap<String, BasePresentation>()
    private val mHandler = HomeHandler(this)

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        setEnableNetworkCheck(true)
        observer()
        timeReceiver()
        initPresentation()
        mViewModel.initSmile()
    }

    private fun initPresentation() {
        mViewModel.showPresentation.value = INPUT_AMOUNT
        mViewModel.presentationMode.value = InputAmountPresentation.FREE_MODE_1
    }

    private fun observer() {
        mViewModel.run {
            smileResultLiveData.observe(this@HomeActivity, Observer {
                if (it.resultCode == Smile2PayResponse.CODE_SUCCESS) {
                    val price = inputPrice.value
                    if (price.isNullOrEmpty()) return@Observer
                    if (presentationMode.value == InputAmountPresentation.FREE_MODE_2) {
                        inputPrice.value = ""
                        addPrice.value = ""
                    }
                    PayActivity.start(this@HomeActivity, it.smile2PayResponse, price)
                } else {
                    val mode = presentationMode.value
                    if (mode == InputAmountPresentation.FREE_MODE_2) {
                        if (it.resultCode == -4 || it.resultCode == -5 || it.resultCode == -15) {
                            //取消返回 显示取消弹窗
                            showCancelView.value = 1
                        } else {
                            presentationMode.value = InputAmountPresentation.FREE_MODE_1
                        }
                    } else if (mode == InputAmountPresentation.QUOTA_MODE_2) {
                        if (it.resultCode == -4) {
                            presentationMode.value = InputAmountPresentation.QUOTA_MODE_1
                            inputPrice.value = ""
                        } else {
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(START_SMILE), 320)
                        }
                    }
                }
            })
            showPresentation.observe(this@HomeActivity, Observer {
                showPresentation(it)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        keyBoardManager.keyBoardCallback = keyBoardCallback
    }

    override fun onPause() {
        super.onPause()
        keyBoardManager.keyBoardCallback = null
    }

    private val keyBoardCallback = object : IKeyBoardCallback {
        override fun keyBoardEvent(event: KeyBoardEvent) {
            mViewModel.run {
                val result = dispatchKeyBoardEvent(event)
                if (result) return
                when (event.type) {
                    KeyBoardType.FUNCTION -> {
                        showPresentation.value = PresentationFactory.INPUT_PASS_WORD
                        verifyType = CanPointSp.VERIFY_FUNCTION
                    }
                    KeyBoardType.CONFIRM -> startSmile()
                    KeyBoardType.SETTING -> {

                    }
                    else -> {
                    }
                }
            }
        }
    }

    /**
     * 事件优先交于副屏 是否拦截
     */
    private fun dispatchKeyBoardEvent(event: KeyBoardEvent): Boolean {
        val showPresentationType = mViewModel.showPresentation.value
        val currentPresentation = presentationMap[showPresentationType]
        return currentPresentation?.interceptKeyEvent(event) ?: false
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
            presentation?.run {
                if (!isShowing) {
                    showPresentation()
                }
            }
        }
    }

    private fun cancelPresentation(type: String) {
        if (presentationMap.containsKey(type)) {
            val presentation = presentationMap[type]
            presentation?.cancelPresentation()
        }
    }

    private class HomeHandler(activity: HomeActivity) : Handler(Looper.getMainLooper()) {
        private val mActivity = WeakReference(activity)
        override fun dispatchMessage(msg: Message?) {
            super.dispatchMessage(msg)
            val get = mActivity.get()
            if (get != null) {
                when (msg?.what) {
                    START_SMILE -> get.mViewModel.startSmile()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timeChangeReceiver?.let { unregisterReceiver(it) }
        presentationMap.forEach { (_, value) ->
            value.cancelPresentation()
        }
    }

    override fun onNetWorkChange(state: Int) {
        mViewModel.netWorkState.value = state
        presentationMap[mViewModel.showPresentation.value]?.showNetworkImg(state.presenterNetWorkStr(), state.presentationNetWorkImg())
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyBoardManager.onKeyDown(keyCode, event)
    }

    companion object {
        const val START_SMILE = 1
    }
}