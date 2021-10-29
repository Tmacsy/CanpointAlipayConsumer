package com.sam.canpoint.ecard.ui.setting

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationChangePasswordResultBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.home.HomeActivity
import com.sam.canpoint.ecard.ui.home.HomeViewModel
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class ChangePasswordResultPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationChangePasswordResultBinding
    private lateinit var mViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_change_password_result, null, false)
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
        mViewModel.changePasswordResult.observe(mContext as LifecycleOwner, Observer { b ->
            mBinding.changeResultIv.setImageResource(if (b != null && b) R.drawable.icon_success else R.drawable.icon_fail)
            mBinding.changeResultTv.text = if (b != null && b) "密码修改成功!" else "密码修改失败!"
            mBinding.changeResultTv.setTextColor(if (b != null && b) ContextCompat.getColor(mContext, R.color.text_success) else ContextCompat.getColor(mContext, R.color.text_failed))
        })
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        if (event.type == KeyBoardType.CANCEL || event.type == KeyBoardType.CONFIRM) {
            cancelPresentation()
            mViewModel.showPresentation.value = PresentationFactory.SETTING
        }
        return true
    }
}