package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationInputPasswordBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.utils.app.ActivityUtils
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

class InputPassWordPresentation(private val mContext: Context, display: Display) :
        BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationInputPasswordBinding
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mHandler: InputPwdHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_input_password, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as AppCompatActivity).createVM(HomeViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
        if (mBinding.tvWarning.isShown) mBinding.tvWarning.visibility = View.GONE
    }

    private fun observer() {
        mViewModel.run {
            passWordError.observe(mContext as LifecycleOwner, Observer {
                mBinding.tvWarning.visibility = View.VISIBLE
            })
            passWord.observe(mContext, Observer {
                if (!TextUtils.isEmpty(it)) {
                    if (mBinding.tvWarning.isShown) {
                        mBinding.tvWarning.visibility = View.GONE
                    }
                }
                mBinding.etPassword.setText(it)
                mBinding.etPassword.setSelection(it.length)
            })
        }
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        val pwd = mViewModel.passWord.value ?: ""
        when (event.type) {
            KeyBoardType.NUM -> {
                if (pwd.length == 6) return true
                mViewModel.passWord.value = pwd + event.num
            }
            KeyBoardType.DELETE -> {
                if (pwd.isEmpty()) return true
                mViewModel.passWord.value = pwd.substring(0, pwd.length - 1)
            }
            KeyBoardType.CANCEL -> doCancel()
            KeyBoardType.CONFIRM -> confirm()
            else -> return true
        }
        return true
    }


    private fun doCancel() {
        mViewModel.passWord.value = ""
        cancelPresentation()
        mViewModel.showPresentation.value = PresentationFactory.INPUT_AMOUNT
    }

    private fun confirm() {
        val value = mViewModel.passWord.value ?: ""
        mViewModel.passWord.value = ""
        mBinding.progress.visibility = View.VISIBLE
        mViewModel.pwdVerify(value, success = {
            mBinding.progress.visibility = View.GONE
            initSuccess()
        }, error = {
            mBinding.progress.visibility = View.GONE
            mViewModel.passWordError.call()
        })
    }

    private fun initSuccess() {
        when (mViewModel.verifyType) {
            CanPointSp.VERIFY_FUNCTION -> {
                mViewModel.showPresentation.value = PresentationFactory.FUNCTION
                delayCancel(320)
            }
            CanPointSp.VERIFY_CHANGE_PASSWORD -> {
                mViewModel.showPresentation.value = PresentationFactory.CHANGE_PASSWORD
                delayCancel(320)
            }
            CanPointSp.VERIFY_EXIT_APP -> {
                ActivityUtils.finishAllActivities()
                exitProcess(0)
            }
        }
    }

    private fun delayCancel(time: Long) {
        if (this::mHandler.isInitialized) {
            mHandler = InputPwdHandler(this)
        }
        mHandler.sendMessageDelayed(mHandler.obtainMessage(), time)
    }

    private class InputPwdHandler(presentation: InputPassWordPresentation) : Handler(Looper.getMainLooper()) {
        private val mPresentation = WeakReference(presentation)
        override fun dispatchMessage(msg: Message?) {
            super.dispatchMessage(msg)
            mPresentation.get()?.cancelPresentation()
        }
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }
}