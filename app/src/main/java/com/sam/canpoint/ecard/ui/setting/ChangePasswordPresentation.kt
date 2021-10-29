package com.sam.canpoint.ecard.ui.setting

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationChangePasswordBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.home.HomeActivity
import com.sam.canpoint.ecard.ui.home.HomeViewModel
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.ref.WeakReference

class ChangePasswordPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {
    private lateinit var mBinding: PresentationChangePasswordBinding
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mHandler: ChangePwdHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_change_password, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as HomeActivity).createVM(HomeViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    private fun observer() {
        mViewModel.run {
            passWord.observe(mContext as LifecycleOwner, Observer {
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

    private fun confirm() {
        val value = mViewModel.passWord.value ?: ""
        mBinding.progress.visibility = View.VISIBLE
        if (value.length != 6) {
            mBinding.tvWarning.visibility = View.VISIBLE
            return
        }
        mViewModel.changePassword(value, success = {
            mViewModel.passWord.value = ""
            CanPointSp.password = value
            mViewModel.showPresentation.value = PresentationFactory.CHANGE_PASSWORD_RESULT
            mViewModel.changePasswordResult.value = true
            delayCancel(320)
        }, error = {
            mViewModel.passWord.value = ""
            mViewModel.showPresentation.value = PresentationFactory.CHANGE_PASSWORD_RESULT
            mViewModel.changePasswordResult.value = false
        })
    }

    private fun doCancel() {
        mViewModel.passWord.value = ""
        cancelPresentation()
        mViewModel.showPresentation.value = PresentationFactory.SETTING
    }

    private fun delayCancel(time: Long) {
        if (this::mHandler.isInitialized) {
            mHandler = ChangePwdHandler(this)
        }
        mHandler.sendMessageDelayed(mHandler.obtainMessage(), time)
    }

    private class ChangePwdHandler(presentation: ChangePasswordPresentation) : Handler(Looper.getMainLooper()) {
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